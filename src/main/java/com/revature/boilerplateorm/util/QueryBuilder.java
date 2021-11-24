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

        System.out.println("Column size is: " + columnValues.size());
        System.out.println("Builder is: " + valueBuilder);
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
        for (int i = 0; i < columns.size(); i++) {
            s.append(columns.get(i)).append(" = ").append("'").append(columnValues.get(i)).append("',");
        }
        return s.substring(0, s.length()-1);
    }

    public <T> T parseResultSet(ResultSet rs, Class<T> type) throws SQLException {
        T parsedObject = null;
        ResultSetMetaData rsm = rs.getMetaData();
        int columnCount = rsm.getColumnCount();

        if(rs.next()) {
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return parsedObject;
    }

}
