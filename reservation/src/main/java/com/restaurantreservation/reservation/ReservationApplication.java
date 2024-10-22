package com.restaurantreservation.reservation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Entry point for the Restaurant Reservation application.
 *
 * <p>This application manages restaurant reservations and includes functionality for managing reservations,
 * restaurants, and customer interactions. It utilizes Spring Boot and is structured using microservices architecture.</p>
 *
 * <p>The application enables Feign clients for communicating with other microservices in the restaurant reservation system.</p>
 */
@SpringBootApplication
@EnableFeignClients(basePackages = "com.restaurantreservation.clients.restaurant")
@Slf4j
public class ReservationApplication {

    /**
     * Main method to start the Spring Boot application.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        log.info("Starting Reservation Application..."); // Log startup message
        SpringApplication.run(ReservationApplication.class, args);
        log.info("Reservation Application started successfully."); // Log successful startup
    }
}
