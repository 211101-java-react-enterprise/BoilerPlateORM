package com.revature.boilerplateorm.util;

import com.revature.boilerplateorm.util.annotations.Column;
import com.revature.boilerplateorm.util.annotations.Id;
import com.revature.boilerplateorm.util.annotations.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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

    //Constructor for Class types
    public QueryBuilder(Class<?> type) {
        this.type = type;
        setTableName();
        setColumnsInfo();
    }

    //Constructor for objects
    public QueryBuilder(Object object) {
        this.object = object;
        this.type = object.getClass();
        setTableName();
        setColumnsInfo();
        setColumnValuesInfo();
    }

    /**
     * Variable "tableName" gets assigned the table in which the object belongs to,
     * specified by an annotation in the object's class.
     */
    private void setTableName() {
        Class<?> clazz;
        try {
            clazz = Class.forName(type.getName());
            if(clazz.isAnnotationPresent(Table.class)) {
                //Get the name specified in the Table annotation if it is present
                Table table = clazz.getAnnotation(Table.class);
                tableName = table.name();
            } else {
                //if not it will just assume the simple name of the class is the table name
                tableName = clazz.getSimpleName();
            }
        } catch (ClassNotFoundException e) {
            logger.error(e.getClass() + ": " + e.getMessage());
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
                //Get the name specified in the Column annotation if it is present
                Column column = field.getAnnotation(Column.class);
                dbName = column.name();
            } else {
                //if not it will just assume the name of the field for the column
                dbName = field.getName();
            }
            columns.add(dbName);
        }
        logger.info("Column: {}", columns);
    }

    /**
     * Get the values for the fields from the object that got passed in and assign it to class instance columnValues
     */
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
                logger.error(e.getClass() + ": " + e.getMessage());
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

        columns.forEach(k -> columnBuilder.append(k).append(","));
        logger.info("Column passed out is: " + columnBuilder.substring(0, columnBuilder.length()-1));
        return columnBuilder.substring(0, columnBuilder.length()-1);
    }

    /**
     * Converts the arrayListed column values appended together into a single string
     * @return String value of a readable query component that consists of the column values
     */
    public String getColumnValues() {

        StringBuilder valueBuilder = new StringBuilder();

        columnValues.forEach(k -> valueBuilder.append("'").append(k).append("',"));
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

    /**
     * Combination of getColumns() and getValues() for the sql UPDATE
     * @return String in the format "Column = Value,", skipping nulls
     */
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

    /**
     * Parses the given ResultSet for GenericDAO#find and GenericDAO#findAll,
     * Invokes given class setter methods on an instance of given class type with given ResultSet values
     */
    public <T> List<T> parseResultSet(ResultSet rs, Class<T> type) throws SQLException {
        T parsedObject;
        ResultSetMetaData rsm = rs.getMetaData();
        int columnCount = rsm.getColumnCount();
        ArrayList<T> parsedObjectList = new ArrayList<>();

        while(rs.next()) {
            try {
                Field[] fields = type.getDeclaredFields();
                T objectInstance = type.newInstance();
                //adding value of whatever we parsed into an array
                for (int i = 1; i <= columnCount; i++) {
                    //-1 here is for compatibility working with 1 and 0 based systems
                    Field field = fields[i-1];
                    //Getting the setX() method from our object's class
                    String methodName = "set" + field.getName().substring(0,1).toUpperCase() + field.getName().substring(1);
                    Method method = type.getMethod(methodName, field.getType());
                    logger.info("Method {} invoking with {}",methodName, rs.getObject(i));
                    method.invoke(objectInstance, rs.getObject(i));
                }
                parsedObject = objectInstance;
                parsedObjectList.add(parsedObject);
            } catch (SQLException | InvocationTargetException | IllegalAccessException e) {
                logger.error(e.getClass() + ": " + e.getMessage());
            } catch (NoSuchMethodException e) {
                logger.error("You are missing a setter method for one of your fields");
            } catch (InstantiationException e) {
                logger.error("You are missing a nullary constructor for your class");
            }
        }
        return parsedObjectList;
    }

    /**
     * parses information from the Object key and call stack, to create the proper where statement for the query
     * If the user created method that calls this is without any specific conditions (find), will default to finding with the primary key
     * Fields are obtained from the user created method, assigning the inputted parameters to the fields in the method name respectively.
     */
    public String getAllWhereStatementsForFind(Object... key) {
        //looks through the call stack to find out the method that calls the queryBuilder's getAllWhereStatements.
        // [0]Thread#getStackTrace -> [1]queryBuilder#getAllWhereElements -> [2]genericDAO#find -> [3]UserDAO#find
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String methodName = stackTraceElements[3].getMethodName();
        StringBuilder outputBuilder = new StringBuilder();
        //Caller method is called find();
        if(methodName.length() == 4) {
            logger.info("This is a find()");
            return getPrimaryKey() + " = " + key[0];
        }
        //removes the findBy part of the method leaving only field names and conjunctions
        String wholeMethod = methodName.substring(6);
        //working with varargs: dependent on the naming of the method that called this
        if(key.length > 1) {
            logger.info("This is a vararg");
            String[] parsedMethod = wholeMethod.split("And");
            for(int i = 0 ; i < parsedMethod.length; i++) {
                String[] tempParsedMethod = parsedMethod[i].split("(?=[A-Z])");
                StringBuilder columnBuilder = new StringBuilder();
                //If it is a multi-word object, put am _ to proper fit snake casing
                for(String z : tempParsedMethod) {
                    columnBuilder.append(z).append("_");
                }
                String fieldName = columnBuilder.substring(0, columnBuilder.length()-1);
                outputBuilder.append(fieldName).append(" = '").append(key[i]).append("'").append(" and ");
            }
            outputBuilder.delete(outputBuilder.length()-5, outputBuilder.length());
        }
        //Working with only a single value, key.length = 1 e.g. findByEmail
         else {
            outputBuilder.append(wholeMethod).append(" = ").append("'").append(key[0]).append("'");
            logger.info("This is a method with a single certain condition, not varargs");
        }
        logger.info(outputBuilder);
        return outputBuilder.toString();
    }

}
