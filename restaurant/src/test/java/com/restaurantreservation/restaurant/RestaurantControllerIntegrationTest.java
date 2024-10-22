package com.restaurantreservation.restaurant;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurantreservation.exception.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link RestaurantController} class.
 *
 * Integration test class for testing RestaurantController in a Spring Boot application.
 * This test uses MockMvc to simulate HTTP requests and mocks the RestaurantService for testing various scenarios.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Import(GlobalExceptionHandler.class)
class RestaurantControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RestaurantService restaurantService;

    @MockBean
    private PagedResourcesAssembler<Restaurant> pagedResourcesAssembler;

    @Autowired
    private ObjectMapper objectMapper;

    private Restaurant restaurant;
    private RestaurantRequest restaurantRequest;

    @BeforeEach
    void setUp() {
        restaurant = Restaurant.builder()
                .id(1L)
                .name("Test Restaurant")
                .address("123 Main St")
                .numberTable(10L)
                .numberTableReserved(0L)
                .build();

        restaurantRequest = new RestaurantRequest("New Restaurant", "456 Elm St", 10L, 0L);
    }

    /**
     * Integration test to verify that all restaurants are retrieved successfully.
     */
    @Test
    void getAllRestaurantsWillReturn_Success() throws Exception {
        // Given: A page of restaurants is returned by the service
        Page<Restaurant> restaurantsPage = new PageImpl<>(Collections.singletonList(restaurant));
        PagedModel.PageMetadata metadata = new PagedModel.PageMetadata(1, 0, 1); // size, page number, total elements
        PagedModel<EntityModel<Restaurant>> pagedModel = PagedModel.of(
                Collections.singletonList(EntityModel.of(restaurant)),
                metadata
        );

        when(restaurantService.getAllRestaurants(any(Pageable.class))).thenReturn(restaurantsPage);
        when(pagedResourcesAssembler.toModel(Mockito.<Page<Restaurant>>any(),
                Mockito.<org.springframework.hateoas.server.RepresentationModelAssembler<Restaurant,
                        EntityModel<Restaurant>>>any())).thenReturn(pagedModel);

        // When: Requesting all restaurants
        mockMvc.perform(get("/api/v1/restaurants")
                        .contentType(MediaType.APPLICATION_JSON))
                // Then: Verify response status and content
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.restaurantList[0].name", is("Test Restaurant")));
    }

    /**
     * Integration test to verify that a restaurant is retrieved successfully by ID.
     */
    @Test
    void getRestaurantByIdWillReturn_Success() throws Exception {
        // Given: The restaurant service returns a restaurant by ID
        when(restaurantService.getRestaurant(anyLong())).thenReturn(restaurant);

        // When: Requesting the restaurant by ID
        mockMvc.perform(get("/api/v1/restaurants/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                // Then: Verify response status and content
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Test Restaurant")));
    }

    /**
     * Integration test to verify that a 404 Not Found response is returned when trying to retrieve a restaurant by an invalid ID.
     */
    @Test
    void getRestaurantByIdWillThrow_NotFound() throws Exception {
        // Given: The restaurant service throws a ResourceNotFoundException for the given ID
        when(restaurantService.getRestaurant(1L)).thenThrow(new ResourceNotFoundException("Restaurant with id [1] not found !!!"));

        // When: Requesting the restaurant by ID
        mockMvc.perform(get("/api/v1/restaurants/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                // Then: Verify 404 response
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Restaurant with id [1] not found !!!")));
    }

    /**
     * Integration test to verify that a table can be reserved successfully.
     */
    @Test
    void reserveTableWillReturn_Success() throws Exception {
        // Given: The service returns a success message for reserving a table
        when(restaurantService.reserveTable(anyLong())).thenReturn("Table reserved in the restaurant [Test Restaurant]");

        // When: Requesting to reserve a table
        mockMvc.perform(put("/api/v1/restaurants/reservation/{restaurantId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                // Then: Verify success response
                .andExpect(status().isOk())
                .andExpect(content().string("Table reserved in the restaurant [Test Restaurant]"));
    }

    /**
     * Integration test to verify that a 400 Bad Request response is returned when trying to reserve a table in a full restaurant.
     */
    @Test
    void reserveTableWillThrow_RestaurantFull() throws Exception {
        // Given: The service throws a RestaurantFullException for reserving a table
        when(restaurantService.reserveTable(anyLong())).thenThrow(new RestaurantFullException());

        // When: Requesting to reserve a table
        mockMvc.perform(put("/api/v1/restaurants/reservation/{restaurantId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                // Then: Verify 400 response
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Table reservation failed, restaurant is full !!!")));
    }

    /**
     * Integration test to verify that a table reservation can be canceled successfully.
     */
    @Test
    void cancelTableReservationWillReturn_Success() throws Exception {
        // Given: The service returns a success message for canceling a reservation
        when(restaurantService.cancelTableReservation(anyLong())).thenReturn("Table reservation canceled in the restaurant [Test Restaurant]");

        // When: Requesting to cancel a reservation
        mockMvc.perform(put("/api/v1/restaurants/cancel_reservation/{restaurantId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                // Then: Verify success response
                .andExpect(status().isOk())
                .andExpect(content().string("Table reservation canceled in the restaurant [Test Restaurant]"));
    }

    /**
     * Integration test to verify that a 400 Bad Request response is returned when trying to cancel a reservation that does not exist.
     */
    @Test
    void cancelTableReservationWillThrow_NoReservations() throws Exception {
        // Given: The service throws a BadRequestException for canceling a non-existent reservation
        when(restaurantService.cancelTableReservation(anyLong())).thenThrow(new BadRequestException("There is no reservations to cancel !!!"));

        // When: Requesting to cancel a reservation
        mockMvc.perform(put("/api/v1/restaurants/cancel_reservation/{restaurantId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                // Then: Verify 400 response
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("There is no reservations to cancel !!!")));
    }

    /**
     * Integration test to verify that a new restaurant can be registered successfully.
     */
    @Test
    void registerRestaurantWillReturn_Success() throws Exception {
        // Given: A restaurantRequest is provided to register a new restaurant
        RestaurantRequest restaurantRequest = new RestaurantRequest("New Restaurant", "New 123 Main St", 15L, 4L);
        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .name(restaurantRequest.name())
                .address(restaurantRequest.address())
                .numberTable(restaurantRequest.numberTable())
                .numberTableReserved(restaurantRequest.numberTableReserved())
                .build();

        EntityModel<Restaurant> restaurantEntityModel = EntityModel.of(restaurant);

        when(restaurantService.register(any(RestaurantRequest.class))).thenReturn(restaurant);

        // When: Requesting to register a new restaurant
        mockMvc.perform(post("/api/v1/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restaurantEntityModel)))
                // Then: Verify success response
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("New Restaurant")))
                .andExpect(jsonPath("$.address", is("New 123 Main St")))
                .andExpect(jsonPath("$.numberTable", is(15)))
                .andExpect(jsonPath("$.numberTableReserved", is(4)));
    }

    /**
     * Integration test to verify that a 409 Conflict response is returned when trying to register a restaurant with a duplicate name.
     */
    @Test
    void registerRestaurantWillThrow_DuplicateName() throws Exception {
        // Given: The service throws a DuplicateResourceException for duplicate restaurant name
        when(restaurantService.register(any(RestaurantRequest.class)))
                .thenThrow(new DuplicateResourceException("Restaurant name already taken !!!"));

        // When: Requesting to register a restaurant
        mockMvc.perform(post("/api/v1/restaurants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restaurantRequest)))
                // Then: Verify 409 response
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", is("Restaurant name already taken !!!")));
    }

    /**
     * Integration test to verify that restaurant information can be updated successfully.
     */
    @Test
    void updateRestaurantWillReturn_Success() throws Exception {
        // Given: The service returns an updated restaurant
        when(restaurantService.updateInformations(any(RestaurantRequest.class), anyLong()))
                .thenReturn(new Restaurant(1L, "update test name", "new address", 22L, 6L));

        // When: Requesting to update restaurant information
        mockMvc.perform(put("/api/v1/restaurants/{restaurantId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(restaurant)))
                // Then: Verify success response
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("update test name")))
                .andExpect(jsonPath("$.address", is("new address")))
                .andExpect(jsonPath("$.numberTable", is(22)))
                .andExpect(jsonPath("$.numberTableReserved", is(6)));
    }

    /**
     * Integration test to verify that a 400 Bad Request response is returned when attempting to update a restaurant with no changes.
     */
    @Test
    void updateRestaurantWillThrow_NoChanges() throws Exception {
        // Given: The service throws a BadRequestException for no data changes
        when(restaurantService.updateInformations(any(RestaurantRequest.class), anyLong()))
                .thenThrow(new BadRequestException("no data changes found"));

        // When: Requesting to update a restaurant with no changes
        mockMvc.perform(put("/api/v1/restaurants/{restaurantId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RestaurantRequest(null, null, null, null))))
                // Then: Verify 400 response
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("no data changes found")));
    }

    /**
     * Integration test to verify that the existence of a restaurant can be checked successfully.
     */
    @Test
    void checkRestaurantExistWillReturn_Exists() throws Exception {
        // Given: The service returns true for restaurant existence check
        when(restaurantService.restaurantExistById(anyLong())).thenReturn(true);

        // When: Requesting to check restaurant existence
        mockMvc.perform(get("/api/v1/restaurants/existence/{restaurantId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                // Then: Verify success response
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    /**
     * Integration test to verify that a 404 Not Found response is returned when checking the existence of a non-existent restaurant.
     */
    @Test
    void checkRestaurantExistWillThrow_NotFound() throws Exception {
        // Given: The service throws a ResourceNotFoundException for a non-existent restaurant
        when(restaurantService.restaurantExistById(1L)).thenThrow(new ResourceNotFoundException("Restaurant with id [1] does not exist."));

        // When: Requesting to check restaurant existence
        mockMvc.perform(get("/api/v1/restaurants/existence/{restaurantId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                // Then: Verify 404 response
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Restaurant with id [1] does not exist.")));
    }

    /**
     * Integration test to verify that the availability of tables in a restaurant can be checked successfully.
     */
    @Test
    void checkAvailabilityWillReturn_Available() throws Exception {
        // Given: The service returns true for table availability check
        when(restaurantService.checkTableAvailability(anyLong())).thenReturn(true);

        // When: Requesting to check table availability
        mockMvc.perform(get("/api/v1/restaurants/check_availability/{restaurantId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                // Then: Verify success response
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    /**
     * Integration test to verify that a 404 Not Found response is returned when checking availability for a non-existent restaurant.
     */
    @Test
    void checkAvailabilityWillThrow_NotFound() throws Exception {
        // Given: The service throws a ResourceNotFoundException for a non-existent restaurant
        when(restaurantService.checkTableAvailability(1L)).thenThrow(new ResourceNotFoundException("Restaurant with id [1] does not exist."));

        // When: Requesting to check table availability
        mockMvc.perform(get("/api/v1/restaurants/check_availability/{restaurantId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                // Then: Verify 404 response
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Restaurant with id [1] does not exist.")));
    }

}