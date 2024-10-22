package com.restaurantreservation.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested resource is not found.
 *
 * <p>This exception is intended to be used in cases where an entity cannot be located
 * in the data source, such as when querying for a non-existing reservation or restaurant.</p>
 */
@Slf4j
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     *
     * @param message The detail message (which is saved for later retrieval by the
     *                {@link Throwable#getMessage()} method).
     */
    public ResourceNotFoundException(String message) {
        super(message);
        log.error("Resource not found: {}", message); // Log the error message when the exception is created
    }
}
