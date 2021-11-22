package com.revature.boilerplateorm;

import com.revature.boilerplateorm.models.User;
import com.revature.boilerplateorm.util.QueryBuilder;
import com.revature.boilerplateorm.util.annotations.Column;
import com.revature.boilerplateorm.util.annotations.Id;
import com.revature.boilerplateorm.util.annotations.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.ArrayList;
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
        User user = new User();
        user.setId("123");
        user.setFirstName("danh");
        user.setLastName("tran");
        user.setEmail("danhtran1337@gmail.com");
        user.setUsername("danhtran123");
        user.setPassword("password");

        QueryBuilder<User> qb = new QueryBuilder<>();

        qb.setTableName(user);

        System.out.println(qb.getTableName());

        qb.setFieldInfo(user);

        System.out.println(qb.getColumns());
        System.out.println(qb.getColumnValues());

    }

}
