package com.restaurantreservation.restaurant;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

/**
 * Base class for contract tests of the RestaurantController.
 * Sets up the necessary mock environment and initializes the RestAssuredMockMvc for testing.
 */
@SpringBootTest(classes = RestaurantApplication.class)
@AutoConfigureMockMvc
public abstract class CdcBaseClass {

    @Autowired
    private RestaurantController restaurantController;

    @MockBean
    private RestaurantService restaurantService;

    /**
     * Sets up the mock environment before each test.
     * Resets the restaurantService mock and configures the RestAssuredMockMvc
     * with the restaurantController. It also mocks necessary behaviors
     * for common test scenarios.
     */
    @BeforeEach
    public void setup() {

        // Reset the mock before each test
        Mockito.reset(restaurantService);

        // Set up the standalone controller for contract testing
        RestAssuredMockMvc.standaloneSetup(restaurantController);

        // Create a mock restaurant
        Restaurant mockRestaurant = Restaurant.builder()
                .id(1L)
                .name("Test Restaurant")
                .numberTable(10L) // Total tables
                .numberTableReserved(5L) // Reserved tables
                .build();

        // Mocking behaviors for restaurant operations
        Mockito.when(restaurantService.getRestaurant(1L))
                .thenReturn(mockRestaurant);

        Mockito.when(restaurantService.restaurantExistById(1L))
                .thenReturn(true);

        Mockito.when(restaurantService.checkTableAvailability(1L))
                .thenReturn(true);

        Mockito.when(restaurantService.reserveTable(1L))
                .thenReturn("Table reserved in the restaurant [Test Restaurant]");

        Mockito.when(restaurantService.cancelTableReservation(1L))
                .thenReturn("Table reservation canceled in the restaurant [Test Restaurant]");

    }

}
