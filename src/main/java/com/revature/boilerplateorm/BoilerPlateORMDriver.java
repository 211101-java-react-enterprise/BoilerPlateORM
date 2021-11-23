package com.revature.boilerplateorm;

import com.revature.boilerplateorm.daos.GenericDAO;
import com.revature.boilerplateorm.models.User;
import com.revature.boilerplateorm.util.ConnectionFactory;
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
        GenericDAO<User> d = null;
        try(Connection conn = ConnectionFactory.getInstance().getConnection()){
            d = new GenericDAO<User>(conn);
            User u = new User();
            u.setEmail("example@email.com");
            u.setFirstName("Test");
            u.setId(1);
            u.setPassword("test");
            u.setUsername("test");
            u.setLastName("testerson");
            //d.save(u);
            d.find(1, u);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Connection failed");
        }
    }

}
