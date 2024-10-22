package com.restaurantreservation.clients.restaurant;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

/**
 * Feign client interface for interacting with the Restaurant service.
 *
 * This interface uses Spring Cloud OpenFeign to create a declarative web service client.
 * It communicates with the Restaurant service to perform operations related to restaurant
 * existence checks, table availability, and reservations.
 */
@FeignClient(name = "restaurant") // Defines the name of the Feign client and indicates the target service
public interface RestaurantClient {

    /**
     * Checks if a restaurant exists by its ID.
     *
     * @param restaurantId the ID of the restaurant to check
     * @return true if the restaurant exists, false otherwise
     */
    @GetMapping(path = "/api/v1/restaurants/existence/{restaurantId}")
    public boolean checkRestaurantExist(@PathVariable("restaurantId") Long restaurantId);

    /**
     * Checks the availability of tables in a restaurant by its ID.
     *
     * @param restaurantId the ID of the restaurant to check availability
     * @return true if tables are available, false otherwise
     */
    @GetMapping(path = "/api/v1/restaurants/check_availability/{restaurantId}")
    public boolean checkAvailability(@PathVariable("restaurantId") Long restaurantId);

    /**
     * Reserves a table in the restaurant by its ID.
     *
     * @param restaurantId the ID of the restaurant where the table will be reserved
     * @return a confirmation message indicating the result of the reservation
     */
    @PutMapping(path = "/api/v1/restaurants/reservation/{restaurantId}")
    public String reserveTable(@PathVariable("restaurantId") Long restaurantId);

    /**
     * Cancels a table reservation in the restaurant by its ID.
     *
     * @param restaurantId the ID of the restaurant where the reservation will be canceled
     * @return a confirmation message indicating the result of the cancellation
     */
    @PutMapping(path = "/api/v1/restaurants/cancel_reservation/{restaurantId}")
    public String cancelTableReservation(@PathVariable("restaurantId") Long restaurantId);
}
