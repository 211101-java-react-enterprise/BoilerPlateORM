package com.revature.boilerplateorm.util;

import com.revature.boilerplateorm.models.User;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class QueryBuilderTest {

    QueryBuilder sut;

    @Before
    public void testSetup() {

    }

    @After
    public void testCleanup() {
        sut = null;
    }

    @Test
    public void test_setTableName_setsTableNameValueToUser_givenUserObject() {

        User newUser = new User(1, "Danh", "Tran", "danhtran1337@gmail.com", "danhtran123", "password");
        String expectedResult = "users";
        sut = new QueryBuilder(newUser);

        String actualResult = sut.getTableName();

        Assert.assertEquals("Expected to set tableName to user given a User object",expectedResult, actualResult);
    }

    //use an actual model TODO insert model with no table annotation
    @Test
    public void test_setTableName_setsTableNameValueToObjectClassName_givenObjectWithNoAnnotation() {
        Object object = new Object();
        String expectedResult = "Object";

        sut = new QueryBuilder(object);

        String actualResult = sut.getTableName();

        Assert.assertEquals("Expected to set tableName to Object given an Object object", expectedResult, actualResult);
    }

    @Test
    public void test_setFieldInfo_setsColumnsAndColumnValuesToUserValue_givenUserObject() {
        User newUser = new User(1, "Danh", "Tran", "danhtran1337@gmail.com", "danhtran123", "password");
        String expectedColumnsResult = "id,first_name,last_name,email,username,password";
        String expectedColumnValuesResult = "1,Danh,Tran,danhtran1337@gmail.com,danhtran123,password";
        sut = new QueryBuilder(newUser);

        String actualColumnsResult = "id,first_name,last_name,email,username,password";
        String actualColumnValuesResult = "1,Danh,Tran,danhtran1337@gmail.com,danhtran123,password";

        Assert.assertEquals("Expected to set ColumnsResult to the column names of user with comma no spaces", expectedColumnsResult, actualColumnsResult);
        Assert.assertEquals("Expected to set ColumnValuesResult to the values of the user with comma no spaces", expectedColumnValuesResult, actualColumnValuesResult);
    }
}
