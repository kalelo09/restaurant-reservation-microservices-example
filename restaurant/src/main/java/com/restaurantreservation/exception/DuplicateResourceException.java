package com.restaurantreservation.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception that is thrown when an attempt is made to create or register a resource
 * that already exists in the system.
 *
 * <p>This exception is a subclass of {@link RuntimeException} and is used to indicate
 * conflicts arising from duplicate resources, such as registering a restaurant with an
 * already existing name.</p>
 *
 * <p>When this exception is thrown, it logs an error message with the details of the conflict
 * for debugging purposes.</p>
 */
@Slf4j
public class DuplicateResourceException extends RuntimeException {

    /**
     * Constructs a new DuplicateResourceException with the specified detail message.
     *
     * @param message the detail message, which is saved for later retrieval by the {@link #getMessage()} method.
     */
    public DuplicateResourceException(String message) {
        super(message);
        log.error("DuplicateResourceException: {}", message); // Log the error message for debugging purposes
    }
}
