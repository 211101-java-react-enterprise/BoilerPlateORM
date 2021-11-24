package com.revature.boilerplateorm;

import com.revature.boilerplateorm.daos.GenericDAO;
import com.revature.boilerplateorm.daos.UserDAO;
import com.revature.boilerplateorm.models.User;
import com.revature.boilerplateorm.util.ConnectionFactory;
import com.revature.boilerplateorm.util.QueryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class BoilerPlateORMDriver {


    /**
     * From what I can tell...
     * Use the annotations to retrieve the class,
     * with that we can get the     class name      ->  table name
     * We can also get the          class fields    ->  column names
     *
     * The last problem to solve is how to create the code, so we
     * can insert these values into the query
     */
    public static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        UserDAO d = null;
        try(Connection conn = ConnectionFactory.getInstance().getConnection()){
            d = new UserDAO(conn);
            User u = new User();
            u.setEmail("example@email.com");
            u.setFirstName("Test");
            u.setId(1);
            u.setPassword("test");
            u.setUsername("test");
            u.setLastName("testerson");
            //d.save(u);
            User testUser = d.find(1,User.class);
            System.out.println(testUser);
            //System.out.printf("Id: %d first_name: %s last_name: %s\n", testUser.getId(), testUser.getFirstName(), testUser.getLastName());
            //System.out.println(d.delete(1, testUser));

            u.setFirstName("asdf");
            System.out.println(d.update(u.getId(), u));
            System.out.println(d.find(1, User.class));
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Connection failed");
        }
    }

}
