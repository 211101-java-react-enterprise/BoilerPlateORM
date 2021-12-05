package com.revature.boilerplateorm.daos;

import com.revature.boilerplateorm.util.QueryBuilder;
import com.revature.boilerplateorm.util.datasource.ConnectionPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.List;
import java.util.Properties;

public class GenericDAO {
    private static ConnectionPool pool = ConnectionPool.getInstance();
    private Connection conn;
    private static final Logger logger = LogManager.getLogger();

    public GenericDAO(Properties props) {
        pool.setPropertiesFile(props);
    }

    //this needs
    public boolean save(Object object) {
        try{
            QueryBuilder qb = new QueryBuilder(object);
            conn = pool.getConnection();
            String sql = "insert into %s (%s) values (%s)";
            sql = String.format(sql,qb.getTableName(),qb.getColumns(), qb.getColumnValuesQuestion());
            logger.info("Save query is looking like: {}", sql);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            qb.prepareSql(pstmt);
            int rowsInserted = pstmt.executeUpdate();
            pool.releaseConnection(conn);
            if (rowsInserted != 0) {
                return true;
            }
        } catch (SQLException e) {
            String s = "Exception: " + e.getClass() + " Error: " + e.getErrorCode() + "Msg: " + e.getMessage();
            logger.error(s);
        }
        return false;
    }

    //this needs
    public <T> T find(Class<T> type, Object... key ){
        try {
            QueryBuilder qb = new QueryBuilder(type);
            conn = pool.getConnection();
            String sql = "select * from %s where %s";
            sql = String.format(sql,qb.getTableName(),qb.getAllWhereStatementsForFind(key));
            logger.info("Find query is looking like: {}", sql);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            qb.prepareSql(pstmt);
            ResultSet rs = pstmt.executeQuery();
            List<T> list = qb.parseResultSet(rs, type);
            pool.releaseConnection(conn);
            if(list.size() > 0) {
                return list.get(0);
            }
        } catch (SQLException e) {
            String s = "Exception: " + e.getClass() + " Error: " + e.getErrorCode() + "Msg: " + e.getMessage();
            logger.error(s);
        }
        return null;
    }

    //this needs
    public <T> List<T> findAll(Class<T> type, Object... key) {
        try {
            QueryBuilder qb = new QueryBuilder(type);
            conn = pool.getConnection();
            String sql = "select * from %s where %s";
            sql = String.format(sql,qb.getTableName(), qb.getAllWhereStatementsForFind(key));
            logger.info("Find query is looking like: {}", sql);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            qb.prepareSql(pstmt);
            ResultSet rs = pstmt.executeQuery();
            pool.releaseConnection(conn);
            return qb.parseResultSet(rs, type);
        } catch (SQLException e) {
            String s = "Exception: " + e.getClass() + " Error: " + e.getErrorCode() + "Msg: " + e.getMessage();
            logger.error(s);
        }
        return null;
    }

    //this needs
    public boolean update(Object object, Object key){
        try{
            QueryBuilder qb = new QueryBuilder(object);
            conn = pool.getConnection();
            String sql = "update %s set %s where %s = %d";
            sql = String.format(sql, qb.getTableName(), qb.getAllColumnEqualQuestion(), qb.getPrimaryKey(), key);
            System.out.println("SQL : " + sql);
            logger.info("Update query is looking like: {}", sql);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt = qb.prepareSql(pstmt);
            System.out.println("Prepared statement: " + pstmt);
            int rows = pstmt.executeUpdate();
            pool.releaseConnection(conn);
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
            conn = pool.getConnection();
            String sql = "select * from %s";
            sql = String.format(sql,qb.getTableName());
            logger.info("Getting all of: {}", type.getSimpleName());
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            pool.releaseConnection(conn);
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
            conn = pool.getConnection();
            String sql = "delete from %s where %s = %d";
            sql = String.format(sql, qb.getTableName(), qb.getPrimaryKey(), key);
            logger.info("Delete query is looking like: {}", sql);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            int updatedRows = pstmt.executeUpdate();
            pool.releaseConnection(conn);
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
