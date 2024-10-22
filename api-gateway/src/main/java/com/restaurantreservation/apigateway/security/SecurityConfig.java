package com.restaurantreservation.apigateway.security;

import com.restaurantreservation.apigateway.security.jwt.JwtAuthenticationManager;
import com.restaurantreservation.apigateway.security.jwt.JwtServerAuthenticationConverter;
import com.restaurantreservation.apigateway.security.jwt.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authorization.AuthorizationContext;

/**
 * Security configuration for the application, enabling WebFlux security
 * and reactive method security.
 *
 * This configuration class defines security settings for handling
 * authentication and authorization in a reactive manner.
 */
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationManager jwtAuthenticationManager;
    private final JwtServerAuthenticationConverter jwtServerAuthenticationConverter;

    /**
     * Constructor for the SecurityConfig class.
     *
     * @param jwtAuthenticationManager the manager responsible for JWT authentication
     * @param jwtUtil utility for handling JWT operations
     */
    public SecurityConfig(JwtAuthenticationManager jwtAuthenticationManager, JwtUtil jwtUtil) {
        this.jwtAuthenticationManager = jwtAuthenticationManager;
        this.jwtServerAuthenticationConverter = new JwtServerAuthenticationConverter(jwtUtil);
    }

    /**
     * Configures the SecurityWebFilterChain for the application.
     *
     * This method sets up the security filter chain, specifying which
     * routes are secured and how authentication is handled.
     *
     * @param http the ServerHttpSecurity object used to configure security settings
     * @param customAuthorizationManager a custom authorization manager for managing access control
     * @return a SecurityWebFilterChain configured for the application
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, ReactiveAuthorizationManager<AuthorizationContext> customAuthorizationManager) {
        AuthenticationWebFilter jwtAuthFilter = new AuthenticationWebFilter(jwtAuthenticationManager);
        jwtAuthFilter.setServerAuthenticationConverter(jwtServerAuthenticationConverter);

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/auth/login").permitAll()
                        .pathMatchers("/api/v1/reservations/**").hasAnyRole("USER", "ADMIN")
                        .pathMatchers("/api/v1/restaurants/**").hasRole("ADMIN")
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    /**
     * Provides a custom authorization manager for managing access control.
     *
     * This method creates an authorization manager that checks if the user
     * is authenticated before granting access to protected resources.
     *
     * @return a ReactiveAuthorizationManager that decides if access should be granted
     */
    @Bean
    public ReactiveAuthorizationManager<AuthorizationContext> customAuthorizationManager() {
        return (authentication, context) -> authentication
                .map(auth -> new AuthorizationDecision(auth.isAuthenticated()))
                .defaultIfEmpty(new AuthorizationDecision(false));
    }
}
