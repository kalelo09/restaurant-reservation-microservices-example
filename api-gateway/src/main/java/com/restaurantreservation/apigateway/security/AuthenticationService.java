package com.restaurantreservation.apigateway.security;

import com.restaurantreservation.apigateway.security.jwt.JwtUtil;
import lombok.AllArgsConstructor;
import org.apache.http.auth.InvalidCredentialsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service for handling user authentication.
 * This service performs authentication by checking provided credentials
 * against a predefined set of in-memory users.
 */
@Service
@AllArgsConstructor
public class AuthenticationService {

    private JwtUtil jwtUtil;

    /**
     * Authenticates the user based on the provided credentials.
     *
     * This method checks the username and password from the provided
     * AuthenticationRequest against in-memory users. If the credentials
     * are valid, a JWT token is generated and returned. If invalid,
     * an InvalidCredentialsException is thrown.
     *
     * @param authRequest the authentication request containing username and password
     * @return the generated JWT token if authentication is successful
     * @throws InvalidCredentialsException if the provided credentials are invalid
     */
    public String authenticate(AuthenticationRequest authRequest) throws InvalidCredentialsException {
        Optional<String> tokenOptional = localAuthentication(authRequest);
        if (tokenOptional.isEmpty()) {
            throw new InvalidCredentialsException("Invalid credentials");
        }
        return tokenOptional.get();
    }

    /**
     * Performs local authentication by checking the provided credentials
     * against predefined in-memory users.
     *
     * @param authRequest the authentication request containing username and password
     * @return an Optional containing the generated JWT token if authentication is successful,
     *         or an empty Optional if authentication fails
     */
    private Optional<String> localAuthentication(AuthenticationRequest authRequest) {
        String adminUsername = "Admin";
        String adminPassword = "passwordAdmin";
        String userUsername = "User";
        String userPassword = "passwordUser";

        // Check if the provided credentials match any of the in-memory users
        if ((authRequest.username().equals(adminUsername) && authRequest.password().equals(adminPassword)) ||
                (authRequest.username().equals(userUsername) && authRequest.password().equals(userPassword))) {
            // Generate a JWT token if the credentials are valid
            String token = jwtUtil.generateToken(authRequest.username());
            return Optional.of(token);
        }
        return Optional.empty();
    }
}
