package com.restaurantreservation.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * Exception thrown when a bad request is made to the application.
 *
 * <p>This exception is intended to be used in scenarios where the client sends
 * an invalid request, such as missing required fields, invalid data format,
 * or other violations of request structure.</p>
 *
 * <p>It extends {@link RuntimeException}, allowing for unchecked exceptions
 * that can be thrown without being declared in the method signature.</p>
 */
@Slf4j
public class BadRequestException extends RuntimeException {

    /**
     * Constructs a new BadRequestException with the specified detail message.
     *
     * @param message The detail message (which is saved for later retrieval by the
     *                {@link Throwable#getMessage()} method).
     */
    public BadRequestException(String message) {
        super(message);
        log.warn("Bad request: {}", message); // Log the warning message when the exception is created
    }
}
