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
            u.setUsername("tester");
            u.setLastName("testerson");

            User u1 = new User();
            u1.setEmail("example10@email.com");
            u1.setFirstName("Test");
            u1.setId(10);
            u1.setPassword("test");
            u1.setUsername("tester1");
            u1.setLastName("testerson");

            User u2 = new User();
            u2.setEmail("example@email.com");
            u2.setFirstName("Test");
            u2.setId(1);
            u2.setPassword("test");
            u2.setUsername("tester4");
            u2.setLastName("testerson");

            User partialUser = new User();
            partialUser.setId(1);
            partialUser.setUsername("tester");

            User namedUser = new User();
            namedUser.setFirstName("Tester");
            namedUser.setLastName("Tester");
            namedUser.setId(1);

            System.out.println(d.save(u1));
            System.out.println(d.findByFirstNameAndLastName(User.class, "Test", "testerson"));
            System.out.println(d.findByUsernameAndPassword(User.class,"test", "test"));
            System.out.println(d.findByEmail("example@email.com", User.class));
            System.out.println(d.find(10, User.class));
            //System.out.println(d.delete(1, partialUser));
            System.out.println(d.update(10, namedUser));
            //System.out.println(d.find(10, User.class));

            //System.out.println(partialUser);
            //List<User> testUser = d.findAll(1,User.class);
            //System.out.println(testUser);
            //System.out.printf("Id: %d first_name: %s last_name: %s\n", testUser.getId(), testUser.getFirstName(), testUser.getLastName());
            //System.out.println(d.delete(0, testUser));

            //u.setFirstName("asdf");
            //System.out.println(d.update(u.getId(), u));


        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Connection failed");
        }
    }

}
