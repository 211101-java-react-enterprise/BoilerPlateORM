package com.revature.boilerplateorm.util.datasource;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ConnectionPool {

    private static Properties props;
    private List<Connection> pool;
    private List<Connection> usedConnections = new ArrayList<>();
    private static int POOL_SIZE = 5;
    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static final ConnectionPool connectionPool = new ConnectionPool();

    private ConnectionPool() {
    }

    public void createPool() {
        List<Connection> holderPool = new ArrayList<>(POOL_SIZE);
        for(int i = 0 ; i < POOL_SIZE; i++) {
            holderPool.add(createConnection());
            System.out.println("Added connection");
        }
        pool = holderPool;
    }

    public static ConnectionPool getInstance() {
        return connectionPool;
    }

    private static Connection createConnection() {

        Connection conn = null;
        try {
            conn = DriverManager.getConnection(props.getProperty("url"), props.getProperty("username"), props.getProperty("password"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public Connection getConnection() {
        Connection conn = pool.remove(pool.size() -1);
        usedConnections.add(conn);
        return conn;
    }

    public boolean releaseConnection(Connection conn) {
        pool.add(conn);
        return usedConnections.remove(conn);
    }

    public void destroyConnection() {
        try {
            for (int i = 0; i < connectionPool.getPoolSize(); i++) {
                connectionPool.getConnection().close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setPropertiesFile(Properties props) {
        ConnectionPool.props = props;
        createPool();
    }

    public int getPoolSize() {
        return pool.size() + usedConnections.size();
    }

}
