package com.restaurantreservation.eurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * EurekaServerApplication is the entry point of the Spring Boot application that acts as a Eureka server.
 *
 * The Eureka server is responsible for service discovery in a microservices architecture,
 * allowing services to register themselves and discover other services.
 */
@SpringBootApplication  // Indicates that this is a Spring Boot application and enables auto-configuration
@EnableEurekaServer  // Enables the Eureka server functionality
public class EurekaServerApplication {

    /**
     * The main method serves as the entry point for the application.
     * It uses SpringApplication.run to launch the application context and start the embedded server.
     *
     * @param args Command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);  // Launch the application
    }

}
