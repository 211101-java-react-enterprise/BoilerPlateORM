# BoilerPlateORM
ORM for Danh and Wilson's P1.

Servlet Endpoints: https://github.com/211101-java-react-enterprise/DURCWebApp

# Overview

A self built ORM (Object-Relational Mapping) used to handle JDBC calls for our DURCWebApp servlets.

# Technologies Used

- PostgreSQL  - 42.2.12
- Log4J       - 2.14.1
- JUnit4      - 4.13.2
- Mockito     - 3.9.0
- Jacoco      - 0.8.7

# Features

- No SQL injections
- Works with any object types
- Built-in connection pool to handle multiple queries at once
- Can query with specific conditions based on user's method name call

# Getting Started

- On the DURCWebApp, create a DB.properties file under src/main/resources with this layout using a PostgreSQL database

```
url= Url of your database
username= Username of your database
password= Password of your database
```
- Download the BoilerPlateORM and run a mvn clean install from the root directory
- Create a DAO/Repository level class and use the ORM.

# Usage

- This ORM implementation uses 4 different annotations to help the GenericDAO create query calls
  - @Entity - Annotation that targets model classes to identify it as a model class.
  - @Table - Annotation that targets model classes to get the name of the table that the model is connected to
  - @Id - Annotation that is used to target the primary key field of the model
  - @Column - Annotation that is used to target the other fields of the model, used to allow user to set the specific name of the column.

- Use built in methods
  - save - Saves the object to the database
  - find - Finds the first result of the query given the id
  - findAll - Finds all the result of the query given the search conditions
  - update - Updates the row of the current object
  - getAll - Retrieve all rows of the particular model
  - delete - Deletes the row containing the given ID

- create your own methods using this format
```
public Model findModelByFieldOneAndFieldTwo() {
    return genericDAO.find(Model.class, fieldOne, fieldTwo);
}

```
  
