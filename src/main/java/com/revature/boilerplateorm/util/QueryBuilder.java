package com.revature.boilerplateorm.util;

import com.revature.boilerplateorm.util.annotations.Column;
import com.revature.boilerplateorm.util.annotations.Id;
import com.revature.boilerplateorm.util.annotations.Table;

import java.lang.reflect.Field;

public class QueryBuilder<T> {

    private String tableName = "";
    private String columns = "";
    private String columnValues = "";

    /**
     * Assign the table in which the object belongs to, to the class variable "tableName"
     * @param object the object to find the particular table in the database it maps to.
     */
    public void setTableName(T object) {
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
            e.printStackTrace();
        }
    }

    /**
     * Assign the field's column name and value to the class variables "columns" and "columnValues" respectively
     * @param object The object to find field information for the database
     */
    public void setFieldInfo(T object) {

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
                e.printStackTrace();
            }

        }
        columns = columnBuilder.substring(0, columnBuilder.length()-1);
        columnValues = valueBuilder.substring(0, valueBuilder.length()-1);
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
