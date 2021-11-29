package com.revature.boilerplateorm;

import com.revature.boilerplateorm.daos.GenericDAO;
import com.revature.boilerplateorm.daos.UserDAO;
import com.revature.boilerplateorm.dtos.Credentials;
import com.revature.boilerplateorm.dtos.Name;
import com.revature.boilerplateorm.models.User;
import com.revature.boilerplateorm.util.ConnectionFactory;
import com.revature.boilerplateorm.util.QueryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

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

            Credentials creds = new Credentials();
            creds.setUsername("tester");
            creds.setPassword("test");

            Name name = new Name();
            name.setFirstName("Danh");
            name.setLastName("Tran");

            //System.out.println(d.findByFirstNameAndLastName(User.class, "Test", "testerson"));
            //System.out.println(d.findByUsernameAndPassword(creds, User.class));
            System.out.println(d.findByEmail("example@email.com", User.class));
            //System.out.println(d.find(1, User.class));
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
