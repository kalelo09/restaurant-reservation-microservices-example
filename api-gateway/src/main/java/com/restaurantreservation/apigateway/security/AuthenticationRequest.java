package com.restaurantreservation.apigateway.security;

/**
 * A record representing an authentication request.
 *
 * This record is used to encapsulate the username and password
 * required for user authentication in the API Gateway module.
 *
 * @param username the username of the user attempting to authenticate
 * @param password the password associated with the username
 */
public record AuthenticationRequest(String username, String password) {
}
