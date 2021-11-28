package com.revature.boilerplateorm.util;

import com.revature.boilerplateorm.util.annotations.Column;
import com.revature.boilerplateorm.util.annotations.Id;
import com.revature.boilerplateorm.util.annotations.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.postgresql.core.Query;
import org.postgresql.core.ResultHandler;
import org.postgresql.core.ResultHandlerBase;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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
    public String getAllWhereStatements(Object key) {
        //looks through the call stack to find out the method that calls the queryBuilder's getAllWhereStatements.
        // [0]Thread#getStackTrace -> [1]queryBuilder#getAllWhereElements -> [2]genericDAO#find -> [3]UserDAO#find
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String methodName = stackTraceElements[3].getMethodName();
        StringBuilder outputBuilder = new StringBuilder();
        //If statement used only if the caller method is called find();
        if(methodName.length() == 4) {
            return getPrimaryKey() + " = " + key;
        }
        //removes the findBy part of the method leaving only field names and conjunctions
        String toParse = methodName.substring(6);
        String[] parsedMethod = toParse.split("(?=[A-Z])");
        //we are working with a DTO
        if(parsedMethod.length > 1) {
            Field[] fields = key.getClass().getDeclaredFields();
            for(Field field : fields) {
                field.setAccessible(true);
                try {
                    outputBuilder.append(field.getName()).append(" = ").append("'").append(field.get(key)).append("'").append(" and ");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                //why the hell did I think of this thing in the comments?!? WHAT WAS THE POINT? WHEN THE DTO HAD THE NAME ALREADY
                //if we wanted to work with varargs instead of DTOs or including DTOs, this could be used as well, not sure if we want to implement that
                //because then it would sorta be tricky to use in that the user will have to input the parameters in the correct order as the naming convention
                //compare the DTO's fields with the fields in the method's name,
                //if they are a match we know th
                /*
                for(String param : parsedMethod) {
                    try {
                        System.out.println(field.getName() + " " + field.get(key));
                        if (field.getName().equals(param.toLowerCase())) {
                            outputBuilder.append(field.getName()).append(" = ").append("'").append(field.get(key)).append("'").append(" and ");
                            break;
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }

                 */
            }
            //remove the last " and " that was appended to the outputBuilder
            outputBuilder.delete(outputBuilder.length()-5, outputBuilder.length());
        } else { // this means we are not working with a DTO but a single int or string
            //this should be for just findByEmail or findByUsername
            String placeholder = parsedMethod[0];
            outputBuilder.append(parsedMethod[0]).append(" = ").append("'").append(key).append("'");
        }
        return outputBuilder.toString();
    }

}
