# Restaurant Reservation Microservice Project example

This project is a microservice architecture for a restaurant reservation system example, featuring a **Restaurant Service** for managing restaurant CRUD operations and a **Reservation Service** for handling reservations, including table availability checks. The services communicate with each other using **Spring Cloud OpenFeign** and are registered with a **Eureka Server** for service discovery. An **API Gateway** secures and routes traffic to the correct microservice, protected by **Spring Security** with JWT authentication.

## Table of Contents

- [Features](#features)
- [Technologies Used](#technologies-used)
- [Why Flyway over Liquibase](#why-flyway-over-liquibase)
- [Why Spring Cloud Contract over Testcontainers](#why-spring-cloud-contract-over-testcontainers)
- [Setup Instructions](#setup-instructions)
- [Running the Application](#running-the-application)
- [Testing the Application](#testing-the-application)
- [Users and Roles](#users-and-roles)
- [API Gateway Routes](#api-gateway-routes)
- [Load Balancing](#load-balancing)
- [Conclusion](#conclusion)

## Features

- **Restaurant Service**: 
  - CRUD operations for restaurant management
  - Checks for restaurant existence and table availability
  - Table reservation and cancellation

- **Reservation Service**: 
  - CRUD operations for reservation management
  - Communicates with Restaurant Service for table availability checks

- **API Gateway**: 
  - Routes requests to the appropriate microservice
  - Secured with Spring Security and JWT authentication

## Technologies Used

- Java 17
- Spring Boot 3.3.4
- Spring Cloud 2023.0.3
- Spring Cloud OpenFeign
- Eureka Server
- Spring Security with JWT
- Flyway for Database Migrations
- JUnit 5 and Mockito for Testing
- Spring Cloud Contract for Contract Testing

## Why Flyway over Liquibase

Flyway is chosen for database migrations due to its simplicity and ease of use. It utilizes a straightforward approach by applying migration scripts in a versioned manner, making it easier to manage and track changes to the database schema. Flyway's integration with Spring Boot is seamless, providing automatic migration management during application startup. Furthermore, its SQL-based migrations allow for greater flexibility and direct control over database changes, which is often preferred in projects with specific database requirements.

## Why Spring Cloud Contract over Testcontainers

Spring Cloud Contract is preferred over Testcontainers for integration testing as it allows for the simulation of service behavior and interactions without the need to manage container lifecycles. This approach promotes a more declarative style of testing by focusing on the contract between services rather than their implementation details. By using Spring Cloud Contract, we can ensure that our services adhere to specified contracts, facilitating easier refactoring and maintenance. Additionally, it improves the speed and reliability of tests by reducing the overhead associated with container management.

## Setup Instructions

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/kalelo09/restaurant-reservation-microservice.git
   cd restaurant-reservation-microservice
2. **Ensure you have the following installed**:
   - Java 17
   - Maven
3. **Build the Project**:
   ```bash
   mvn clean install
4. **Set Up the Database**:
   Flyway is used for database migrations, and the migration scripts will automatically be applied upon starting the application.

## Running the Application

1. **Start the Eureka Server**:
   - Navigate to the eureka-server module:
     ```bash
     cd eureka-server
   - Run:
     ```bash
     mvn spring-boot:run
2. **Start the API Gateway**:
   - Navigate to the api-gateway module:
     ```bash
     cd api-gateway
   - Run:
     ```bash
     mvn spring-boot:run
3. **Start the Restaurant Service**:
   - Navigate to the restaurant-service module:
     ```bash
     cd restaurant
   - Run:
     ```bash
     mvn spring-boot:run
4. **Start the Reservation Service**:
   - Navigate to the reservation-service module:
     ```bash
     cd reservation
   - Run:
     ```bash
     mvn spring-boot:run

## Testing the Application

  - **Unit Testing**: Run the tests with:
     ```bash
     mvn test

## Users and Roles

Two in-memory users are defined for testing:
  - Admin:
    - Username: Admin
    - Password: passwordAdmin
    - Roles: ADMIN, USER
  - User:
    - Username: User
    - Password: passwordUser
    - Roles: USER

## API Gateway Routes

The following routes are configured in the API Gateway for accessing the microservices:

### Login
- **POST** `localhost:8082/auth/login`: Login

### Restaurant Service Routes
- **GET** `localhost:8082/api/v1/restaurants`: List all restaurants
- **GET** `localhost:8082/api/v1/restaurants/{id}`: Retrieve restaurant details by ID
- **GET** `localhost:8082/api/v1/restaurants/check_availability/{id}`: Checking table availability 
- **POST** `localhost:8082/api/v1/restaurants`: Create a new restaurant
- **PUT** `localhost:8082/api/v1/restaurants/{id}`: Update restaurant information

### Reservation Service Routes
- **GET** `localhost:8082/api/v1/reservations`: List all reservations
- **GET** `localhost:8082/api/v1/reservations/restaurant/{idRestaurant}`: List reservations by restaurant
- **GET** `localhost:8082/api/v1/reservations/customer/{customerName}`: List reservations by customer
- **GET** `localhost:8082/api/v1/reservations/canceled`: List reservations canceled
- **GET** `localhost:8082/api/v1/reservations/not_canceled`: List reservations not canceled
- **GET** `localhost:8082/api/v1/reservations/{id}`: Retrieve reservation details by ID
- **POST** `localhost:8082/api/v1/reservations`: Make a new reservation
- **PUT** `localhost:8082/api/v1/reservations/{id}`: Update reservation details
- **PUT** `localhost:8082/api/v1/reservations/cancel/{id}`: Cancel a reservation

## Load Balancing
With the configuration above, the API Gateway acts as a load balancer. It uses Eureka to discover service instances and balance requests among them. You can verify load balancing by running multiple instances of your microservices (using different ports) and monitoring request distribution.

### Running Multiple Instances
To run multiple instances of a microservice:
- Clone the service to a different directory or set up multiple profiles.
- Change the server port in the application.yml

## Conclusion

This microservice project provides a comprehensive simple example for restaurant reservations with secure and efficient operations. The use of Spring Cloud technologies enables robust service communication and scalability, while the testing frameworks ensure reliability and maintainability of the codebase.

Feel free to modify any section as needed, and let me know if there’s anything else you’d like to add!
