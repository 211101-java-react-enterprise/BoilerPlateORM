package com.revature.boilerplateorm.daos;

import com.revature.boilerplateorm.models.User;
import com.revature.boilerplateorm.util.ConnectionFactory;
import com.revature.boilerplateorm.util.QueryBuilder;

import java.sql.*;


public class GenericDAO<T> {
    //TODO, get a connection from a pool when calling this
    private final Connection conn;

    public GenericDAO(Connection conn){
        this.conn = conn;
    }


    public boolean save(T o) {
        try{
            QueryBuilder qb = new QueryBuilder(o);

            String sql = "insert into %s (%s) values (%s)";
            sql = String.format(sql,qb.getTableName(),qb.getColumns(), qb.getColumnValues());
            System.out.println(sql);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            System.out.println(pstmt);
            int rowsInserted = pstmt.executeUpdate();

            if (rowsInserted != 0) {
                return true;
            }
        } catch (SQLException e) {
            // TODO logging
            e.printStackTrace();
        }
        return false;
    }


    public T find(int key, T object){
        try {
            QueryBuilder qb = new QueryBuilder(object);
            String sql = "select * from %s where %s = %d";
            sql = String.format(sql, qb.getTableName(), qb.getPrimaryKey(), key);
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            StringBuilder sb = new StringBuilder();
            if (rs.next()){
                ResultSetMetaData rsm = rs.getMetaData();
                for (int i = 0; i < rsm.getColumnCount(); i++) {
                    //TODO
                    continue;
                }
                System.out.println(sb);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
