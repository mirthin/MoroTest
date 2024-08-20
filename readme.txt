MoroTest
MoroTest is a Spring Boot application designed to manage users with authentication and authorization features. It provides RESTful APIs for user management, including creating, updating, and deleting users, as well as password management.

Features
	User Management: Create, read, update, and delete user accounts.
	Authentication and Authorization: Secured endpoints with basic authentication.
	Password Management: Password hashing with BCrypt for secure storage.
	Role-Based Access Control: Two roles are supported, USER and ADMIN.
Project Structure
	Controller: Handles HTTP requests and responses for user-related operations.
	Service: Contains business logic for managing users.
	DAO: Defines the User entity and its persistence configuration.
	Configuration: Sets up Spring Security for authentication and authorization.
Technologies
	Spring Boot: Framework for building Java-based applications.
	Spring Security: Provides authentication and authorization.
	PostgreSQL: Database used for storing user information.
	Lombok: Reduces boilerplate code with annotations.
	Jakarta Persistence (JPA): ORM for database operations.
	Jakarta Validation: Ensures data integrity through validation annotations.
Prerequisites
	Java 17 or higher
	PostgreSQL database
	Maven 3.6 or higher
	Spring Boot 3.3.2 or higher


Setup
1. Clone the Repository

git clone https://github.com/mirthin/MoroTest.git
cd MoroTest


2. Configure the Database
Update src/main/resources/application.properties with your PostgreSQL database details:

spring.application.name=MoroTest
spring.datasource.url=jdbc:postgresql://localhost:5432/db_moro
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update

Replace your_db_username and your_db_password with your actual PostgreSQL credentials.

Create database table in postgreSql

CREATE TABLE user_table(
	id BIGINT PRIMARY KEY,
	name VARCHAR(50),
	username VARCHAR(50) UNIQUE NOT NULL,
	password VARCHAR(255) 
);


3. Build the Project
Use Maven to build the project:

mvn clean install

4. Run the Application
You can run the Spring Boot application using:

mvn spring-boot:run

Alternatively, you can build a runnable JAR and execute it:

mvn package 
java -jar target/MoroTest-0.0.1-SNAPSHOT.jar

Endpoints:

GET /api/users
Retrieve all users or a specific user by ID.

GET /api/users/{id}
Retrieve a user by their ID.

POST /api/users
Create a new user. Password must be provided and will be hashed.

PUT /api/users
Update user information. Only the authenticated user or an admin can update a user.

PUT /api/users/password
Update a user's password. Requires authentication.

DELETE /api/users/delete
Delete a user. Only the authenticated user or an admin can delete a user.

Testing and Utility Endpoints

GET /api/users/test
Test endpoint for basic connectivity.

DELETE /api/users/deleteall
Delete all users from the database (for testing purposes).