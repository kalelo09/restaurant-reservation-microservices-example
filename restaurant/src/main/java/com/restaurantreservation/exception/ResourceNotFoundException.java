package com.restaurantreservation.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception that is thrown when a requested resource cannot be found in the system.
 *
 * <p>This exception is a subclass of {@link RuntimeException} and is used to indicate
 * that an operation was attempted on a resource that does not exist, such as trying
 * to retrieve a restaurant that cannot be found by its identifier.</p>
 *
 * <p>When this exception is thrown, it logs an error message with the details of the
 * missing resource for debugging purposes.</p>
 */
@Slf4j
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     *
     * @param message the detail message, which is saved for later retrieval by the {@link #getMessage()} method.
     */
    public ResourceNotFoundException(String message) {
        super(message);
        log.error("ResourceNotFoundException: {}", message); // Log the error message for debugging purposes
    }
}
