package com.restaurantreservation.restaurant;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for the Reservation service.
 *
 * <p>This class provides configuration for the application's components, specifically
 * the setup of a {@link RestTemplate} bean used for making REST API calls.
 * The RestTemplate is annotated with {@link LoadBalanced}, enabling client-side load balancing
 * using Spring Cloud's load balancer (such as Netflix Ribbon or Spring Cloud LoadBalancer).</p>
 *
 * <ul>
 *   <li>{@code @Configuration} marks this class as a source of bean definitions for the application context.</li>
 *   <li>The {@code restTemplate} method creates and configures a {@link RestTemplate} bean.</li>
 *   <li>The {@code @LoadBalanced} annotation allows the {@link RestTemplate} to resolve service names (registered in Eureka or another service registry)
 *   and balance the requests across instances of a service.</li>
 * </ul>
 *
 * <p>This configuration is useful in microservice architectures where the application needs to interact
 * with other services by resolving service names and distributing requests among multiple instances.</p>
 *
 * @see RestTemplate
 * @see LoadBalanced
 * @see Configuration
 */
@Configuration
public class RestaurantLoadBalancerConfig {
    /**
     * Creates a {@link RestTemplate} bean for making RESTful HTTP requests.
     *
     * <p>The {@link LoadBalanced} annotation enables client-side load balancing, meaning the {@link RestTemplate}
     * will automatically distribute the requests among different instances of a service registered with a service registry like Eureka.</p>
     *
     * @return a {@link RestTemplate} instance with load balancing capabilities.
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
