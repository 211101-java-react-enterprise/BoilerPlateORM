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


    public User find(int key, User user){
        try {
            //todo
            QueryBuilder qb = new QueryBuilder(user);
            String sql = "select * from %s where %s = %d";
            sql = String.format(sql,qb.getTableName(),qb.getPrimaryKey(), key);
            System.out.println(sql);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next())
            return new User(
            rs.getInt("id"),
            rs.getString("first_name"),
            rs.getString("last_name"),
            rs.getString("email"),
            rs.getString("username"),
            rs.getString("password")
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
