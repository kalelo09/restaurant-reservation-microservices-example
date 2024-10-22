package com.restaurantreservation.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the restaurant reservation application.
 *
 * <p>This class centralizes the handling of exceptions thrown in the application. It provides
 * a consistent response format for various exceptions that may occur during the execution of
 * the application, such as validation errors, resource not found, and data integrity violations.</p>
 *
 * <p>Each exception handler method is annotated with {@link ExceptionHandler} to specify which
 * exception type it handles. When an exception of that type is thrown, the corresponding method
 * will be invoked to return an appropriate HTTP response.</p>
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles BadRequestException.
     *
     * @param ex the BadRequestException thrown
     * @return ResponseEntity with error message and HTTP status 400 BAD REQUEST
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, String>> badRequestException(BadRequestException ex) {
        log.error("BadRequestException: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Handles ResourceNotFoundException.
     *
     * @param ex the ResourceNotFoundException thrown
     * @return ResponseEntity with error message and HTTP status 404 NOT FOUND
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("ResourceNotFoundException: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Handles DuplicateResourceException.
     *
     * @param ex the DuplicateResourceException thrown
     * @return ResponseEntity with error message and HTTP status 409 CONFLICT
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Map<String, String>> duplicateResourceException(DuplicateResourceException ex) {
        log.error("DuplicateResourceException: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
    }

    /**
     * Handles RestaurantFullException.
     *
     * @param ex the RestaurantFullException thrown
     * @return ResponseEntity with error message and HTTP status 400 BAD REQUEST
     */
    @ExceptionHandler(RestaurantFullException.class)
    public ResponseEntity<Map<String, String>> restaurantFullException(RestaurantFullException ex) {
        log.error("RestaurantFullException: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Handles IllegalArgumentException.
     *
     * @param ex the IllegalArgumentException thrown
     * @return ResponseEntity with error message and HTTP status 400 BAD REQUEST
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> illegalArgumentException(IllegalArgumentException ex) {
        log.error("IllegalArgumentException: {}", ex.getMessage());
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Handles MethodArgumentNotValidException, which occurs during validation errors.
     *
     * @param ex the MethodArgumentNotValidException thrown
     * @return ResponseEntity with validation error messages and HTTP status 400 BAD REQUEST
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.error("Validation errors: {}", errors);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles TransactionSystemException related to JPA transactions.
     *
     * @param ex the TransactionSystemException thrown
     * @return ResponseEntity with error messages and HTTP status 400 BAD REQUEST
     */
    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<Map<String, String>> handleTransactionException(TransactionSystemException ex) {
        Throwable cause = ex.getCause().getCause();
        Map<String, String> errors = new HashMap<>();
        StringBuilder errorsMessages = new StringBuilder();

        if (cause instanceof ConstraintViolationException) {
            ConstraintViolationException validationException = (ConstraintViolationException) cause;
            validationException.getConstraintViolations().forEach(violation -> {
                String fieldName = violation.getPropertyPath().toString();
                String errorMessage = violation.getMessage();
                errorsMessages.append(" ").append(errorMessage).append(",");
            });
            errors.put("message", errorsMessages.deleteCharAt(errorsMessages.lastIndexOf(",")).toString().trim());
        } else {
            errors.put("message", "Transaction error occurred");
        }

        log.error("TransactionSystemException: {}", errors);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles DataIntegrityViolationException, indicating a data integrity issue.
     *
     * @param ex the DataIntegrityViolationException thrown
     * @return ResponseEntity with error message and HTTP status 400 BAD REQUEST
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "Data integrity violation: " + ex.getMostSpecificCause().getMessage());
        log.error("DataIntegrityViolationException: {}", errors);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Creates a standardized error response.
     *
     * @param status the HTTP status to return
     * @param message the error message
     * @return ResponseEntity with the error message and specified HTTP status
     */
    private ResponseEntity<Map<String, String>> createErrorResponse(HttpStatus status, String message) {
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return new ResponseEntity<>(response, status);
    }
}
