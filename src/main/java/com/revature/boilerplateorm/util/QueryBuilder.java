package com.revature.boilerplateorm.util;

import com.revature.boilerplateorm.util.annotations.Column;
import com.revature.boilerplateorm.util.annotations.Id;
import com.revature.boilerplateorm.util.annotations.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;

public class QueryBuilder {

    Logger logger = LogManager.getLogger();
    Object object;
    private String tableName = "";
    private String columns = "";
    private String columnValues = "";

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
        StringBuilder columnBuilder = new StringBuilder();
        StringBuilder valueBuilder = new StringBuilder();

        for(Field field : fields) {
            boolean isPrimaryKey = false;
            field.setAccessible(true);

            //check if current field is the primary key
            if(field.isAnnotationPresent(Id.class)) {
                isPrimaryKey = true;
            }

            String dbName;
            //find out the table name mapping to the current field
            if(field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                dbName = column.name();
            } else {
                dbName = field.getName();
            }

            columnBuilder.append(dbName).append(",");
            try {
                valueBuilder.append(field.get(object)).append(",");
            } catch (IllegalAccessException e) {
                logger.error(e.getMessage());
            }

        }

        columns = columnBuilder.substring(0, columnBuilder.length()-1);
        columnValues = valueBuilder.substring(0, valueBuilder.length()-1);

        logger.info("Column: {}", columns);
        logger.info("Values: {}", columnValues);
    }

    public String getTableName() {
        return tableName;
    }

    public String getColumns() {
        return columns;
    }

    public String getColumnValues() {
        return columnValues;
    }

}
