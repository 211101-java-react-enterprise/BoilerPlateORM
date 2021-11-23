package com.revature.boilerplateorm;

import com.revature.boilerplateorm.daos.GenericDAO;
import com.revature.boilerplateorm.models.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
        User u = new User();
        u.setEmail("example@email.com");
        u.setFirstName("Test");
        u.setId("asdawfrafsfawa");
        u.setPassword("test");
        u.setUsername("test");
        u.setLastName("testerson");
        GenericDAO d = new GenericDAO();

        d.find(1, u);
    }

}
