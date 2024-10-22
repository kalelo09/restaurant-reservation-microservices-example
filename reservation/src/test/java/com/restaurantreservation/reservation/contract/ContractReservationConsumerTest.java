package com.restaurantreservation.reservation.contract;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static java.util.Objects.requireNonNull;
import static org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties.StubsMode.LOCAL;

/**
 * Integration tests for the contract-based interactions with the Restaurant service.
 *
 * <p>This class verifies the behavior of the Restaurant service API by performing integration tests
 * that check the reservation functionalities using a local stub runner.</p>
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@AutoConfigureStubRunner(
        stubsMode = StubRunnerProperties.StubsMode.LOCAL,
        ids = "com.restaurantservice:restaurant:+:8090") // Update with your actual group ID, artifact ID, and port
public class ContractReservationConsumerTest {

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Tests the table reservation functionality.
     * <p>
     * Given a request to reserve a table at the restaurant, when called, it verifies that the reservation
     * is successful and returns the expected confirmation message.
     * </p>
     */
    @Test
    void testReserveTableSuccessfully() {
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:8090/api/v1/restaurants/reservation/1",
                HttpMethod.PUT,
                null,
                String.class);

        // Assert that the response status is OK and the expected body is returned
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(requireNonNull(response.getBody())).isEqualTo("Table reserved in the restaurant [Test Restaurant]");
    }

    /**
     * Tests the existence check of a restaurant.
     * <p>
     * Given a request to check if a restaurant exists, when called, it verifies that the service returns
     * an OK status and confirms the existence of the restaurant.
     * </p>
     */
    @Test
    void testCheckRestaurantExistence() {
        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");

        // Send a GET request to check the existence of the restaurant
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:8090/api/v1/restaurants/existence/1",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                String.class
        );

        // Assert that the response status code is OK
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Convert the response body to Boolean and assert
        Boolean exists = Boolean.valueOf(response.getBody());
        assertThat(exists).isTrue();
    }

    /**
     * Tests the table availability check for a restaurant.
     * <p>
     * Given a request to check table availability at a restaurant, when called, it verifies that the service returns
     * an OK status and confirms the availability of the table.
     * </p>
     */
    @Test
    void testCheckTableAvailability() {
        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");

        // Send a GET request to check table availability
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:8090/api/v1/restaurants/check_availability/1",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );

        // Assert that the response status code is OK
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        // Convert the response body to Boolean and assert
        Boolean exists = Boolean.valueOf(response.getBody());
        assertThat(exists).isTrue();
    }

    /**
     * Tests the cancellation of a reservation.
     * <p>
     * Given a request to cancel a reservation, when called, it verifies that the cancellation is successful
     * and returns the expected confirmation message.
     * </p>
     */
    @Test
    void testCancelReservationSuccessfully() {
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:8090/api/v1/restaurants/cancel_reservation/1",
                HttpMethod.PUT,
                null,
                String.class);

        // Assert that the response status is OK and the expected body is returned
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(requireNonNull(response.getBody())).isEqualTo("Table reservation canceled in the restaurant [Test Restaurant]");
    }
}