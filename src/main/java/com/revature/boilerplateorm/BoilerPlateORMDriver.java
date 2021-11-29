package com.revature.boilerplateorm;

import com.revature.boilerplateorm.daos.UserDAO;
import com.revature.boilerplateorm.models.User;
import com.revature.boilerplateorm.util.ConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class BoilerPlateORMDriver {

    public static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        UserDAO d = null;
        try(Connection conn = ConnectionFactory.getInstance().getConnection()){
            d = new UserDAO();
            User u = new User();
            u.setEmail("example@email.com");
            u.setFirstName("Test");
            u.setId(1);
            u.setPassword("test");
            u.setUsername("test");
            u.setLastName("testerson");

            User partialUser = new User();
            partialUser.setId(1);
            partialUser.setUsername("tester");

            System.out.println(d.findByFirstNameAndLastName(User.class, "Test", "testerson"));
            System.out.println(d.findByUsernameAndPassword(User.class,"test", "test"));
            System.out.println(d.findByEmail("example@email.com", User.class));
            System.out.println(d.find(1, User.class));
            //d.delete(1, partialUser);

            //System.out.println(partialUser);

            //d.update(1, partialUser);
            //d.save(u);
            //List<User> testUser = d.findAll(1,User.class);
            //System.out.println(testUser);
            //System.out.printf("Id: %d first_name: %s last_name: %s\n", testUser.getId(), testUser.getFirstName(), testUser.getLastName());
            //System.out.println(d.delete(1, testUser));

            //u.setFirstName("asdf");
            //System.out.println(d.update(u.getId(), u));


        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Connection failed");
        }
    }

}
