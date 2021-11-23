package com.revature.boilerplateorm.daos;

import com.revature.boilerplateorm.models.User;
import com.revature.boilerplateorm.util.ConnectionFactory;
import com.revature.boilerplateorm.util.QueryBuilder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class UserDAO {
    //TODO, get a connection from a pool when calling this


    public User save(User newUser) {
        try(Connection conn = ConnectionFactory.getInstance().getConnection()){
            QueryBuilder qb = new QueryBuilder(newUser);

            String sql = "insert into %s (%s) values (%s)";
            sql = String.format(sql,qb.getTableName(),qb.getColumns(), qb.getColumnValues());
            System.out.println(sql);
            PreparedStatement pstmt = conn.prepareStatement(sql);
            System.out.println(pstmt);
            int rowsInserted = pstmt.executeUpdate();

            if (rowsInserted != 0) {
                return newUser;
            }

        } catch (SQLException e) {
            // TODO logging
            e.printStackTrace();

        }

        return null;

    }
}
