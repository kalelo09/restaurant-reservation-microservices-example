package com.restaurantreservation.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * Custom exception that is thrown when a bad request is made to the application.
 *
 * <p>This exception is a subclass of {@link RuntimeException} and is used to indicate
 * that the server could not understand the request due to invalid syntax or parameters.</p>
 *
 * <p>When this exception is thrown, it logs an error message with the details of the bad request.</p>
 */
@Slf4j
public class BadRequestException extends RuntimeException {

    /**
     * Constructs a new BadRequestException with the specified detail message.
     *
     * @param message the detail message, which is saved for later retrieval by the {@link #getMessage()} method.
     */
    public BadRequestException(String message) {
        super(message);
        log.error("BadRequestException: {}", message); // Log the error message for debugging purposes
    }
}
