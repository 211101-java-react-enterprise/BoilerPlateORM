package com.revature.boilerplateorm.daos;

import com.revature.boilerplateorm.util.QueryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.List;

public class GenericDAO {
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
            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted != 0) {
                return true;
            }
        } catch (SQLException e) {
            String s = "Exception: " + e.getClass() + " Error: " + e.getErrorCode() + "Msg: " + e.getMessage();
            logger.error(s);
        }
        return false;
    }

    public <T> T find(Class<T> type, Object... key ){
        try {
            QueryBuilder qb = new QueryBuilder(type);
            String sql = "select * from %s where %s";
            sql = String.format(sql,qb.getTableName(),qb.getAllWhereStatementsForFind(key));
            logger.info("Find query is looking like: {}", sql);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            List<T> list = qb.parseResultSet(rs, type);
            if(list.size() > 0) {
                return list.get(0);
            }
        } catch (SQLException e) {
            String s = "Exception: " + e.getClass() + " Error: " + e.getErrorCode() + "Msg: " + e.getMessage();
            logger.error(s);
        }
        return null;
    }

    public <T> List<T> findAll(Class<T> type, Object... key) {
        try {
            QueryBuilder qb = new QueryBuilder(type);
            String sql = "select * from %s where %s";
            sql = String.format(sql,qb.getTableName(), qb.getAllWhereStatementsForFind(key));
            logger.info("Find query is looking like: {}", sql);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            return qb.parseResultSet(rs, type);
        } catch (SQLException e) {
            String s = "Exception: " + e.getClass() + " Error: " + e.getErrorCode() + "Msg: " + e.getMessage();
            logger.error(s);
        }
        return null;
    }

    public boolean update(Object object, Object key){
        try{
        QueryBuilder qb = new QueryBuilder(object);
        String sql = "update %s set %s where %s = %d";
        sql = String.format(sql, qb.getTableName(), qb.getColumnEqualValues(), qb.getPrimaryKey(), key);
        logger.info("Update query is looking like: {}", sql);
        int rows = conn.prepareStatement(sql).executeUpdate();
        if (rows > 0) return true;
        } catch (SQLException e) {
            String s = "Exception: " + e.getClass() + " Error: " + e.getErrorCode() + "Msg: " + e.getMessage();
            logger.error(s);
        }
        return false;
    }


    public <T> List<T> getAll(Class<T> type){
        try {
            QueryBuilder qb = new QueryBuilder(type);
            String sql = "select * from %s";
            sql = String.format(sql,qb.getTableName());
            logger.info("Getting all of: {}", type.getSimpleName());
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            return qb.parseResultSet(rs, type);
        } catch (SQLException e) {
            String s = "Exception: " + e.getClass() + " Error: " + e.getErrorCode() + "Msg: " + e.getMessage();
            logger.error(s);
        }
        return null;
    }

    public boolean delete(Object object, Object key) {
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
            String s = "Exception: " + e.getClass() + " Error: " + e.getErrorCode() + "Msg: " + e.getMessage();
            logger.error(s);
        }
        return false;
    }

}
