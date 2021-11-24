package com.revature.boilerplateorm.daos;

import com.revature.boilerplateorm.models.User;
import com.revature.boilerplateorm.util.QueryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;


public abstract class GenericDAO {
    //TODO, get a connection from a pool when calling this
    private final Connection conn;
    private static final Logger logger = LogManager.getLogger();

    public GenericDAO(Connection conn) {
        this.conn = conn;
    }


    public boolean save(Object object) {
        try{
            QueryBuilder qb = new QueryBuilder(object);

            String sql = "insert into %s (%s) values (%s)";
            sql = String.format(sql,qb.getTableName(),qb.getColumns(), qb.getColumnValues());
            logger.info("Save query is looking like: {}", sql);
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

    //instead of object we need to take the class type since we might not know th
    public <T> T find(int key, Class<T> type){
        try {
            //todo
            QueryBuilder qb = new QueryBuilder(type);
            String sql = "select * from %s where %s = %d";
            sql = String.format(sql,qb.getTableName(),qb.getPrimaryKey(), key);
            logger.info("Find query is looking like: {}", sql);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            return qb.parseResultSet(rs, type);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean update(int key, Object object){
        try{
        //Complete object updates only, use only if you have a whole object
        QueryBuilder qb = new QueryBuilder(object);
        String sql = "update %s set %s where %s = %d";
        sql = String.format(sql, qb.getTableName(), qb.getColumnEqualValues(), qb.getPrimaryKey(), key);
        logger.info("Update query is looking like: {}", sql);
        int rows = conn.prepareStatement(sql).executeUpdate();
        if (rows > 0) return true;
        } catch (SQLException e) {
            // TODO logging
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int key, Object object) {
        try {
            if (object == null) {
                logger.info("User is trying to delete an unknown object");
                return false;
            }
            QueryBuilder qb = new QueryBuilder(object);

            String sql = "delete from %s where %s = %d";
            sql = String.format(sql, qb.getTableName(), qb.getPrimaryKey(), key);
            logger.info("Delete query is looking like: {}", sql);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            int updatedRows = pstmt.executeUpdate();
            if(updatedRows == 1) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
