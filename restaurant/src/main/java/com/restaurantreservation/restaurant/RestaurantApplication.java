package com.restaurantreservation.restaurant;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Restaurant Reservation application.
 *
 * <p>This application manages restaurant operations, including registering restaurants,
 * handling reservations, and checking availability. It utilizes Spring Boot and is structured
 * within a microservices architecture to facilitate communication and interaction with other services.</p>
 *
 * <p>The application is designed to provide a seamless experience for customers and restaurant
 * owners, enabling efficient management of reservations and restaurant details.</p>
 */
@Slf4j
@SpringBootApplication
public class RestaurantApplication {

    /**
     * Main method to start the Spring Boot application.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        log.info("Starting Restaurant Reservation application..."); // Log startup message
        SpringApplication.run(RestaurantApplication.class, args);
        log.info("Restaurant Reservation application started successfully."); // Log successful startup
    }
}
