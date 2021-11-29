package com.revature.boilerplateorm.util;

import com.revature.boilerplateorm.util.annotations.Column;
import com.revature.boilerplateorm.util.annotations.Id;
import com.revature.boilerplateorm.util.annotations.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QueryBuilder {

    Logger logger = LogManager.getLogger();
    Object object;
    Class<?> type;
    private String tableName = "";
    private String primaryKey;
    private final ArrayList<String> columns = new ArrayList<>();
    private final ArrayList<String> columnValues = new ArrayList<>();

    public QueryBuilder(Class<?> type) {
        this.type = type;
        setTableName();
        setColumnsInfo();
    }

    public QueryBuilder(Object object) {
        this.object = object;
        this.type = object.getClass();
        setTableName();
        setColumnsInfo();
        setColumnValuesInfo();
    }

    /**
     * Assign the table in which the object belongs to, to the class variable "tableName"
     */
    private void setTableName() {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(type.getName());
            if(clazz.isAnnotationPresent(Table.class)) {
                Table table = clazz.getAnnotation(Table.class);
                tableName = table.name();
            } else {
                tableName = clazz.getSimpleName();
            }
        } catch (ClassNotFoundException e) {
            logger.error(e.getMessage());
        }
        logger.info("Working on table: {}", tableName);
    }

    /**
     * Assign the field's column name and value to the class variables "columns" and "columnValues" respectively
     */
    private void setColumnsInfo() {

        Field[] fields = type.getDeclaredFields();
        for(Field field : fields) {
            field.setAccessible(true);
            setPrimaryKey(field);
            String dbName;
            //find out the table name mapping to the current field
            if(field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                dbName = column.name();
            } else {
                dbName = field.getName();
            }
            columns.add(dbName);
        }
        logger.info("Column: {}", columns);
    }

    private void setColumnValuesInfo() {

        Field[] fields = type.getDeclaredFields();
        for(Field field : fields) {
            field.setAccessible(true);
            try {
                if(field.get(object) == null) {
                    columnValues.add(null);
                    continue;
                }
                columnValues.add(field.get(object).toString());
            } catch (IllegalAccessException e) {
                logger.error(e.getMessage());
            }
        }

        logger.info("Values: {}", columnValues);
    }

    public String getTableName() {
        return tableName;
    }

    /**
     * Converts the arrayListed columns appended together into a single string
     * @return String value of a readable query component that consists of the columns
     */
    public String getColumns() {

        StringBuilder columnBuilder = new StringBuilder();

        for(int i = 0; i < columns.size(); i++) {
            columnBuilder.append(columns.get(i)).append(",");
        }

        logger.info("Column passed out is: " + columnBuilder.substring(0, columnBuilder.length()-1));
        return columnBuilder.substring(0, columnBuilder.length()-1);
    }

    /**
     * Converts the arrayListed column values appended together into a single string
     * @return String value of a readable query component that consists of the column values
     */
    public String getColumnValues() {

        StringBuilder valueBuilder = new StringBuilder();

        for (int i = 0; i < columnValues.size(); i++) {
            valueBuilder.append("'").append(columnValues.get(i)).append("',");
        }
        logger.info("Value passed out is: " + valueBuilder.substring(0, valueBuilder.length()-1));
        return valueBuilder.substring(0,valueBuilder.length()-1);
    }

    public void setPrimaryKey(Field field) {
        //check if current field is the primary key
        if(field.isAnnotationPresent(Id.class)) {
            primaryKey = field.getName();
        }
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public String getColumnEqualValues() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < columnValues.size(); i++) {
            //selective updates
            if(columnValues.get(i) == null) {
                logger.info("We're skipping this one " + columns.get(i) + " " + columnValues.get(i));
                continue;
            }
            logger.info("We're adding this one " + columns.get(i) + " " + columnValues.get(i));
            s.append(columns.get(i)).append(" = ").append("'").append(columnValues.get(i)).append("',");
        }
        return s.substring(0, s.length()-1);
    }

    //this should maybe return a list of type T
    public <T> List<T> parseResultSet(ResultSet rs, Class<T> type) throws SQLException {
        T parsedObject = null;
        ResultSetMetaData rsm = rs.getMetaData();
        int columnCount = rsm.getColumnCount();
        ArrayList<T> parsedObjectList = new ArrayList<>();

        while(rs.next()) {
            try {
                Field[] fields = type.getDeclaredFields();
                T objectInstance = type.newInstance();
                //adding value of whatever we parsed into an array
                for (int i = 1; i <= columnCount; i++) {
                    Field field = fields[i-1];
                    String methodName = "set" + field.getName().substring(0,1).toUpperCase() + field.getName().substring(1);
                    Method method = type.getMethod(methodName, field.getType());
                    logger.info("Method {} invoking with {}",methodName, rs.getObject(i));
                    method.invoke(objectInstance, rs.getObject(i));
                }
                parsedObject = objectInstance;
                parsedObjectList.add(parsedObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return parsedObjectList;
    }

    /**
     * parses information from the Object key and call stack, to create the proper where statement for the query
     * If the user created method that calls this is without any specific conditions (find), will default to finding with the primary key
     * If it is created with specific conditions, or right now if the parameter is a DTO, then it will find whatever fields the DTO stores
     * If it is created with a single specific condition, it will take the field from the user created method, since the input will be an int or String.
     */
    public String getAllWhereStatements(Object... key) {
        //looks through the call stack to find out the method that calls the queryBuilder's getAllWhereStatements.
        // [0]Thread#getStackTrace -> [1]queryBuilder#getAllWhereElements -> [2]genericDAO#find -> [3]UserDAO#find
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String methodName = stackTraceElements[3].getMethodName();
        StringBuilder outputBuilder = new StringBuilder();
        //If statement used only if the caller method is called find();
        if(methodName.length() == 4) {
            logger.info("This is a find()");
            return getPrimaryKey() + " = " + key[0];
        }
        //removes the findBy part of the method leaving only field names and conjunctions
        String toParse = methodName.substring(6);
        //working with varargs: dependent on the naming of the method that called this
        if(key.length > 1) {
            logger.info("This is a vararg");
            String[] parsedMethod = toParse.split("And");
            String[] tempParsedMethod;
            String[] realArray = new String[parsedMethod.length];
            for(int i = 0 ; i < parsedMethod.length; i++) {
                tempParsedMethod = parsedMethod[i].split("(?=[A-Z])");
                StringBuilder columnBuilder = new StringBuilder();
                //I hate this because this means this only works with snake casing naming convention
                //If we had a "configuration" then this could be fixed but that is out of the scope of this project.
                for(String z : tempParsedMethod) {
                    columnBuilder.append(z).append("_");
                }
                realArray[i] = columnBuilder.substring(0, columnBuilder.length()-1);
                outputBuilder.append(realArray[i]).append(" = '").append(key[i]).append("'").append(" and ");
            }
            outputBuilder.delete(outputBuilder.length()-5, outputBuilder.length());
        }
        /*
        //Working with a DTO
        //else if(key[0].getClass().isAnnotationPresent(DTO.class))... Since this is a custom ORM... we don't necessarily have to follow how Hibernate does it.
        //We can have our own annotations, so we could make an annotations exclusively for DTOs just to make this a bit easier
        //Because right now we are facing the problem of this isn't exclusively for DTOs since Strings, Ints etc also have their own fields
        //Maybe we can just rearrange this with the else statement under this
        else if(key[0].getClass().getDeclaredFields().length > 1) {
            logger.info("this is a DTO");
            //get the fields of the DTO to parse
            Field[] fields = key[0].getClass().getDeclaredFields();
            for(Field field : fields) {
                field.setAccessible(true);
                try {
                    outputBuilder.append(field.getName()).append(" = ").append("'").append(field.get(key[0])).append("'").append(" and ");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            //remove the last " and " that was appended to the outputBuilder
            outputBuilder.delete(outputBuilder.length()-5, outputBuilder.length());
         }
         */
         else { // this means we are not working with a DTO but a single int or string
            //this should be for just findByEmail or findByUsername
            outputBuilder.append(toParse).append(" = ").append("'").append(key[0]).append("'");
            logger.info("This is a method with a single certain condition, not varargs");
        }
        logger.info(outputBuilder);
        return outputBuilder.toString();
    }

}
