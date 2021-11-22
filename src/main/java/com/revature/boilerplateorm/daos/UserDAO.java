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

            String sql = "insert into ? (?) values (?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, qb.getTableName());
            pstmt.setString(2, qb.getColumns());
            pstmt.setString(3, qb.getColumnValues());

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
