package com.revature.boilerplateorm.util;

import com.revature.boilerplateorm.util.annotations.Column;
import com.revature.boilerplateorm.util.annotations.Id;
import com.revature.boilerplateorm.util.annotations.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.postgresql.core.ResultHandler;
import org.postgresql.core.ResultHandlerBase;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class QueryBuilder {

    Logger logger = LogManager.getLogger();
    Object object;
    private String tableName = "";
    private String primaryKey;
    private final ArrayList<String> columns = new ArrayList<>();
    private final ArrayList<String> columnValues = new ArrayList<>();

    public QueryBuilder() {

    }
    public QueryBuilder(Object object) {
        this.object = object;
        setTableName();
        setFieldInfo();
    }

    /**
     * Assign the table in which the object belongs to, to the class variable "tableName"
     */
    private void setTableName() {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(object.getClass().getName());
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
    private void setFieldInfo() {

        Field[] fields = object.getClass().getDeclaredFields();

        for(Field field : fields) {
            field.setAccessible(true);

            //check if current field is the primary key
            if(field.isAnnotationPresent(Id.class)) {
                primaryKey = field.getName();
            }

            String dbName;
            //find out the table name mapping to the current field
            if(field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                dbName = column.name();
            } else {
                dbName = field.getName();
            }
            columns.add(dbName);

            try {
                columnValues.add(field.get(object).toString());

            } catch (IllegalAccessException e) {
                logger.error(e.getMessage());
            }

        }
        //valueBuilder.append(field.get(object)).append(",");
        //columns = columnBuilder.substring(0, columnBuilder.length()-1);
       // columnValues = valueBuilder.substring(0, valueBuilder.length()-1);

        logger.info("Column: {}", columns);
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

    public Object getPrimaryKey() {
        return primaryKey;
    }

    public String getColumnEqualValues() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < columns.size(); i++) {
            s.append(columns.get(i)).append(" = ").append(columnValues.get(i)).append(", ");
        }
        return s.substring(0, s.length()-2);
    }

    public Object parseResultSet(ResultSet rs, Object object) throws SQLException {
        Object newObject = null;
        int columnCount = 0;
        ResultSetMetaData rsm = rs.getMetaData();
        columnCount = rsm.getColumnCount();
        Object[] argArray = new Object[columnCount];

        if(rs.next()) {
            //adding value of whatever we parsed into an array
            for (int i = 1; i <= columnCount; i++) {
                Object e = rs.getObject(i);
                argArray[i-1] = e;
            }
            try {
                newObject = object.getClass().newInstance();
                Constructor[] constructors = object.getClass().getConstructors();
                for (Constructor ctor : constructors) {
                    //if we find a constructor that has an arg size of columnCount, pass the array into the constructor making a new instance of class with values.
                    if(ctor.getParameterCount() == columnCount) {
                        newObject = ctor.newInstance(argArray);
                        break;
                    }
                }
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return newObject;

    }

}
