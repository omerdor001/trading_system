Trading System
===================

Description
-----------

This project is a Spring Boot application built using Maven. It demonstrates a System for buying products from different stores (examples: ebay, amazon , ...)

Prerequisites
-------------

*   Java Development Kit (JDK) version 22 or higher
*   Apache Maven installed and configured
*   IDE (e.g., IntelliJ IDEA, Eclipse) for development (optional but recommended)

Getting Started
---------------

To run this project locally, follow these steps:

1.  **Clone the repository**
### Client
```http
git clone https://github.com/omerdor001/trading_system.git
cd .\online-store\
npm install
ng serve run
```

2. Build the project
### Server
```
 mvn clean install
 ```
3. Run the application
```
mvn spring-boot:run
```
4. Access the application
Once the application has started, open a web browser and go to: ```http://localhost:8080```

Configuration File
The configuration file (application.properties) is located in the src/main/resources directory. It defines the hostname and initial state of the application. Here's an example:

properties

```
# application.properties
# Server port
server.port=8080

# Database configuration
spring.datasource.url=jdbc:mysql://localhost:3306/mydatabase
spring.datasource.username=root
spring.datasource.password=*********************************

# Hostname configuration
app.hostname=localhost

# Initial state configuration
app.initialState=enabled

```
Modify the server.port, spring.datasource.* properties, app.hostname, and app.initialState according to your environment and requirements.

Additional Notes
Make sure to configure the database URL, username, and password in the application.properties file according to your database setup.
Customize other properties and dependencies in the pom.xml file as needed for your project.
Support
For questions, issues, or feedback, please contact [Manager] at [Manager@Trading_systems_ORG.com].

