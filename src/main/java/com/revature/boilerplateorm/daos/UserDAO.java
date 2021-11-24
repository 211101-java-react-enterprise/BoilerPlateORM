package com.revature.boilerplateorm.daos;

import com.revature.boilerplateorm.models.User;
import com.revature.boilerplateorm.util.QueryBuilder;

import java.sql.*;

public class UserDAO extends GenericDAO{
    private final Connection conn;

    public UserDAO(Connection conn){
        super(conn);
        this.conn = conn;
    }

}
