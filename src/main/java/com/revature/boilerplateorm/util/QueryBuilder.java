package com.revature.boilerplateorm.util;

import com.revature.boilerplateorm.util.annotations.Column;
import com.revature.boilerplateorm.util.annotations.Id;
import com.revature.boilerplateorm.util.annotations.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class QueryBuilder {

    Logger logger = LogManager.getLogger();
    Object object;
    private String tableName = "";
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

    public String getColumns() {

        StringBuilder columnBuilder = new StringBuilder();

        for(int i = 0; i < columns.size(); i++) {
            columnBuilder.append(columns.get(i)).append(",");
        }

        logger.info("Column passed out is: " + columnBuilder.substring(0, columnBuilder.length()-1));
        return columnBuilder.substring(0, columnBuilder.length()-1);
    }

    public String getColumnValues() {

        StringBuilder valueBuilder = new StringBuilder();

        for (int i = 0; i < columnValues.size(); i++) {
            valueBuilder.append("'").append(columnValues.get(i)).append("',");
        }

        logger.info("Value passed out is: " + valueBuilder.substring(0, valueBuilder.length()-1));
        return valueBuilder.substring(0,valueBuilder.length()-1);
    }

}
