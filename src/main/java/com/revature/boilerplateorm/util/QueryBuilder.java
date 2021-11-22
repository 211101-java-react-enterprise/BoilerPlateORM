package com.revature.boilerplateorm.util;

import com.revature.boilerplateorm.util.annotations.Column;
import com.revature.boilerplateorm.util.annotations.Id;
import com.revature.boilerplateorm.util.annotations.Table;

import java.lang.reflect.Field;

public class QueryBuilder<T> {

    private String tableName = "";
    private String columns = "";
    private String columnValues = "";

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

    public void setFieldInfo(T object) {

        Field[] fields = object.getClass().getDeclaredFields();
        StringBuilder columnBuilder = new StringBuilder();
        StringBuilder valueBuilder = new StringBuilder();

        for(Field field : fields) {
            boolean isPrimaryKey = false;
            field.setAccessible(true);

            if(field.isAnnotationPresent(Id.class)) {
                isPrimaryKey = true;
            }

            String dbName;
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
