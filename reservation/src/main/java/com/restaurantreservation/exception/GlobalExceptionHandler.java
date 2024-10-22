package com.restaurantreservation.exception;

import feign.FeignException;
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
 * <p>This class is annotated with {@link RestControllerAdvice}, which makes it a centralized
 * exception handler for all controllers in the application. It captures specific exceptions
 * and returns appropriate HTTP responses.</p>
 *
 * <p>Each method is responsible for handling a specific type of exception and logging the error.</p>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles {@link BadRequestException} and returns a 400 Bad Request response.
     *
     * @param ex The exception thrown.
     * @return A ResponseEntity containing the error message.
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, String>> badRequestException(BadRequestException ex) {
        log.error("Bad Request Exception: {}", ex.getMessage()); // Log the exception message
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles {@link ResourceNotFoundException} and returns a 404 Not Found response.
     *
     * @param ex The exception thrown.
     * @return A ResponseEntity containing the error message.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("Resource Not Found Exception: {}", ex.getMessage()); // Log the exception message
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles {@link IllegalArgumentException} and returns a 400 Bad Request response.
     *
     * @param ex The exception thrown.
     * @return A ResponseEntity containing the error message.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> illegalArgumentException(IllegalArgumentException ex) {
        log.error("Illegal Argument Exception: {}", ex.getMessage()); // Log the exception message
        Map<String, String> response = new HashMap<>();
        response.put("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles {@link FeignException} and returns a 400 Bad Request response.
     *
     * @param ex The exception thrown.
     * @return A ResponseEntity containing the error message.
     */
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map<String, String>> feignException(FeignException ex) {
        String message = gettingFeignExceptionMessageOnly(ex.getMessage());
        log.error("Feign Exception: {}", message); // Log the exception message
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles validation errors from @RequestBody and returns a 400 Bad Request response.
     *
     * @param ex The exception thrown.
     * @return A ResponseEntity containing the validation errors.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("Validation Exception: {}", ex.getMessage()); // Log the exception message
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles JPA transaction-related exceptions and returns a 400 Bad Request response.
     *
     * @param ex The exception thrown.
     * @return A ResponseEntity containing the error messages.
     */
    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<Map<String, String>> handleTransactionException(TransactionSystemException ex) {
        log.error("Transaction System Exception: {}", ex.getMessage()); // Log the exception message
        Throwable cause = ex.getCause().getCause();
        Map<String, String> errors = new HashMap<>();
        StringBuilder errorsMessages = new StringBuilder();

        if (cause instanceof ConstraintViolationException) {
            ConstraintViolationException validationException = (ConstraintViolationException) cause;
            validationException.getConstraintViolations().forEach(violation -> {
                String fieldName = violation.getPropertyPath().toString();
                String errorMessage = violation.getMessage();
                errorsMessages.append(" " + errorMessage + ",");
            });
            errors.put("message", errorsMessages.deleteCharAt(errorsMessages.lastIndexOf(",")).toString().trim());
        }

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles data integrity violations and returns a 400 Bad Request response.
     *
     * @param ex The exception thrown.
     * @return A ResponseEntity containing the error message.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.error("Data Integrity Violation Exception: {}", ex.getMostSpecificCause().getMessage()); // Log the exception message
        Map<String, String> errors = new HashMap<>();
        errors.put("error", "Data integrity violation: " + ex.getMostSpecificCause().getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Extracts and formats the message from a Feign exception.
     *
     * @param message The original exception message.
     * @return A formatted message extracted from the Feign exception.
     */
    private String gettingFeignExceptionMessageOnly(String message) {
        return message.substring(message.lastIndexOf("message") + 10, message.lastIndexOf(']') - 2);
    }
}
