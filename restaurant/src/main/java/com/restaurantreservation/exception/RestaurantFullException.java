package com.restaurantreservation.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception that is thrown when an attempt is made to reserve a table
 * in a restaurant that has reached its full capacity.
 *
 * <p>This exception is a subclass of {@link RuntimeException} and indicates
 * that no more reservations can be accepted for the specified restaurant.</p>
 *
 * <p>When this exception is thrown, it logs an error message indicating
 * the failure to reserve a table due to the restaurant being full.</p>
 */
@Slf4j
public class RestaurantFullException extends RuntimeException {

    /**
     * Constructs a new RestaurantFullException with a default message.
     */
    public RestaurantFullException() {
        super("Table reservation failed, restaurant is full !!!"); // Default error message
        log.error("RestaurantFullException: Table reservation failed, restaurant is full !!!"); // Log the error for debugging purposes
    }
}
