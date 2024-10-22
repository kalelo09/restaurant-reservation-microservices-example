package com.restaurantreservation.apigateway.security;

import lombok.AllArgsConstructor;
import org.apache.http.auth.InvalidCredentialsException;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for handling authentication requests.
 * This controller provides an endpoint for user login and
 * delegates the authentication logic to the AuthenticationService.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    /**
     * Authenticates a user based on the provided credentials.
     *
     * This endpoint accepts a POST request with an authentication
     * request containing the username and password. It calls the
     * authenticate method from the AuthenticationService to perform
     * the authentication logic and returns the generated JWT token.
     *
     * @param authRequest the authentication request containing username and password
     * @return the generated JWT token if authentication is successful
     * @throws InvalidCredentialsException if the provided credentials are invalid
     */
    @PostMapping("/login")
    public String login(@RequestBody AuthenticationRequest authRequest) throws InvalidCredentialsException {
        return authenticationService.authenticate(authRequest);
    }
}
