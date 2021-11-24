package com.revature.boilerplateorm.daos;

import com.revature.boilerplateorm.models.User;
import com.revature.boilerplateorm.util.ConnectionFactory;
import com.revature.boilerplateorm.util.QueryBuilder;

import java.sql.*;
import java.util.List;

public class UserDAO{
    private final Connection conn = ConnectionFactory.getInstance().getConnection();

    /*
    public UserDAO(Connection conn) {
        this.conn = conn;
    }

     */

    private final GenericDAO gDao = new GenericDAO(conn);

    public boolean save(User user) {
        return gDao.save(user);
    }

    public <T> T find(int key, Class<T> type) {
        return gDao.find(key, type);
    }

    public boolean update(int key, User user) {
        return gDao.update(key, user);
    }

    public boolean delete(int key, User user) {
        return gDao.delete(key, user);
    }

    public <T>List<T> findAll(Object key, Class<T> type) {
        return gDao.findAll(key, type);
    }

}
