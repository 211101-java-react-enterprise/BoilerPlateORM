package com.revature.boilerplateorm.daos;

import com.revature.boilerplateorm.models.User;
import com.revature.boilerplateorm.util.QueryBuilder;

import java.sql.*;


public abstract class GenericDAO {
    //TODO, get a connection from a pool when calling this
    private final Connection conn;

    public GenericDAO(Connection conn) {
        this.conn = conn;
    }


    public boolean save(Object o) {
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

    public boolean update(int key,Object o){
        try{
        //Complete object updates only, use only if you have a whole object
        QueryBuilder qb = new QueryBuilder(o);
        String sql = "update %s set (%s) where %s = %d";
        sql = String.format(sql, qb.getTableName(), qb.getColumns(), qb.getPrimaryKey(), key);

        int rows = conn.prepareStatement(sql).executeUpdate();
        if (rows > 0) return true;
        } catch (SQLException e) {
            // TODO logging
            e.printStackTrace();
        }
        return false;
    }

    public ResultSet find(int key, User user){
        try {
            //todo
            QueryBuilder qb = new QueryBuilder(user);
            String sql = "select * from %s where %s = %d";
            sql = String.format(sql,qb.getTableName(),qb.getPrimaryKey(), key);
            System.out.println(sql);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
