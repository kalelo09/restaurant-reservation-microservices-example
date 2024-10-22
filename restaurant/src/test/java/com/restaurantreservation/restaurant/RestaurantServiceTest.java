package com.restaurantreservation.restaurant;

import com.restaurantreservation.exception.BadRequestException;
import com.restaurantreservation.exception.DuplicateResourceException;
import com.restaurantreservation.exception.ResourceNotFoundException;
import com.restaurantreservation.exception.RestaurantFullException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the RestaurantService class.
 */
class RestaurantServiceTest {

    private RestaurantRepository restaurantRepository;
    private RestaurantService restaurantService;

    @BeforeEach
    void setUp() {
        // Initialize the mock RestaurantRepository and the RestaurantService before each test
        restaurantRepository = Mockito.mock(RestaurantRepository.class);
        restaurantService = new RestaurantService(restaurantRepository);
    }

    /**
     * Test case for retrieving all restaurants.
     * Verifies that a pageable request returns a non-empty page of restaurants.
     */
    @Test
    void getAllRestaurantsWillReturnPageOfRestaurants() {
        // Given: A pageable request and a sample restaurant to return
        Pageable pageable = Pageable.ofSize(10);
        Restaurant restaurant = new Restaurant(1L, "Test Restaurant", "123 Main St", 10L, 0L);
        Page<Restaurant> restaurantPage = new PageImpl<>(List.of(restaurant));
        when(restaurantRepository.findAll(pageable)).thenReturn(restaurantPage);

        // When: Calling the method to get all restaurants
        Page<Restaurant> result = restaurantService.getAllRestaurants(pageable);

        // Then: Verify the result is not empty and contains the expected restaurant
        assertThat(result).isNotEmpty();
        assertThat(result.getContent()).containsExactly(restaurant);
    }

    /**
     * Test case for retrieving a restaurant by ID.
     * Verifies that the correct restaurant entity is returned.
     */
    @Test
    void getRestaurantWillReturnRestaurant() {
        // Given: An existing restaurant ID and the corresponding restaurant entity
        Long restaurantId = 1L;
        Restaurant restaurant = new Restaurant(restaurantId, "Test Restaurant", "123 Main St", 10L, 0L);
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        // When: Calling the method to retrieve the restaurant by ID
        Restaurant result = restaurantService.getRestaurant(restaurantId);

        // Then: Verify that the returned restaurant matches the expected entity
        assertThat(result).isEqualTo(restaurant);
    }

    /**
     * Test case for retrieving a restaurant by ID when the restaurant does not exist.
     * Expects a ResourceNotFoundException to be thrown.
     */
    @Test
    void getRestaurantWillThrowResourceNotFoundExceptionWhenNotFound() {
        // Given: A restaurant ID that does not exist
        Long restaurantId = 1L;
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        // When & Then: Expecting a ResourceNotFoundException when calling the method
        assertThatExceptionOfType(ResourceNotFoundException.class)
                .isThrownBy(() -> restaurantService.getRestaurant(restaurantId))
                .withMessage("Restaurant with id [1] not found !!!");
    }

    /**
     * Test case for registering a new restaurant.
     * Verifies that a valid request results in a registered restaurant.
     */
    @Test
    void registerWillReturnRegisteredRestaurant() {
        // Given: A new restaurant request and mock behavior for saving
        RestaurantRequest request = new RestaurantRequest("New Restaurant", "456 Elm St", 10L, 0L);
        Restaurant savedRestaurant = new Restaurant(1L, request.name(), request.address(), request.numberTable(), request.numberTableReserved());
        when(restaurantRepository.existsRestaurantByName(request.name())).thenReturn(false);
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(savedRestaurant);

        // When: Registering the new restaurant
        Restaurant result = restaurantService.register(request);

        // Then: Verify that the returned restaurant matches the saved restaurant
        assertThat(result).isEqualTo(savedRestaurant);
    }

    /**
     * Test case for registering a new restaurant with an existing name.
     * Expects a DuplicateResourceException to be thrown.
     */
    @Test
    void registerWillThrowDuplicateResourceExceptionWhenNameExists() {
        // Given: A new restaurant request with a name that already exists
        RestaurantRequest request = new RestaurantRequest("Existing Restaurant", "456 Elm St", 10L, 0L);
        when(restaurantRepository.existsRestaurantByName(request.name())).thenReturn(true);

        // When & Then: Expecting a DuplicateResourceException when trying to register
        assertThatExceptionOfType(DuplicateResourceException.class)
                .isThrownBy(() -> restaurantService.register(request))
                .withMessage("Restaurant name already taken !!!");
    }

    /**
     * Test case for updating restaurant information.
     * Verifies that the restaurant is updated correctly.
     */
    @Test
    void updateInformationsWillReturnUpdatedRestaurant() {
        // Given: An existing restaurant ID and a request for updating its information
        Long restaurantId = 1L;
        Restaurant existingRestaurant = new Restaurant(restaurantId, "Old Restaurant", "123 Main St", 10L, 0L);
        RestaurantRequest request = new RestaurantRequest("Updated Restaurant", "123 Main New St", 15L, 0L);
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(existingRestaurant));
        when(restaurantRepository.save(existingRestaurant)).thenReturn(existingRestaurant);

        // When: Updating the restaurant's information
        Restaurant result = restaurantService.updateInformations(request, restaurantId);

        // Then: Verify the restaurant's name and number of tables are updated correctly
        assertThat(result.getName()).isEqualTo("Updated Restaurant");
        assertThat(result.getAddress()).isEqualTo("123 Main New St");
        assertThat(result.getNumberTable()).isEqualTo(15);
    }

    /**
     * Test case for updating restaurant information with invalid table count.
     * Expects an IllegalArgumentException to be thrown.
     */
    @Test
    void updateInformationsWillThrowIllegalArgumentException() {
        // Given: An existing restaurant ID and a request for updating its information
        Long restaurantId = 1L;
        Restaurant existingRestaurant = new Restaurant(restaurantId, "Old Restaurant", "123 Main St", 10L, 5L);
        RestaurantRequest request = new RestaurantRequest("Updated Restaurant", "123 Main St", 4L, 0L);
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(existingRestaurant));

        // When: Updating the restaurant's information

        // Then: Verify the restaurant's name and number of tables are updated correctly
        assertThatThrownBy(() -> restaurantService.updateInformations(request, restaurantId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Number of tables should be superior or equal to the number of tables reserved. restaurant got [%s] tables reserved".formatted(existingRestaurant.getNumberTableReserved()));
    }

    /**
     * Test case for updating restaurant information with an existing name.
     * Expects a DuplicateResourceException to be thrown.
     */
    @Test
    void updateInformationsWillThrowDuplicateResourceException() {
        // Given: An existing restaurant ID and a request for updating its information
        Long restaurantId = 1L;
        Restaurant existingRestaurant = new Restaurant(restaurantId, "Old Restaurant", "123 Main St", 10L, 5L);
        RestaurantRequest request = new RestaurantRequest("Updated Restaurant", "123 Main St", 12L, 0L);
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(existingRestaurant));
        when(restaurantRepository.existsRestaurantByName(request.name())).thenReturn(true);

        // When: Updating the restaurant's information

        // Then: Verify the restaurant's name and number of tables are updated correctly
        assertThatThrownBy(() -> restaurantService.updateInformations(request, restaurantId))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Restaurant name already taken !!!");
    }

    /**
     * Test case for updating restaurant information with no changes.
     * Expects a BadRequestException to be thrown.
     */
    @Test
    void updateInformationsWillThrowBadRequestExceptionWhenNoChangesFound() {
        // Given: An existing restaurant ID and a request with no changes
        Long restaurantId = 1L;
        Restaurant existingRestaurant = new Restaurant(restaurantId, "Existing Restaurant", "123 Main St", 10L, 0L);
        RestaurantRequest request = new RestaurantRequest("Existing Restaurant", "123 Main St", 10L, 0L);
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(existingRestaurant));

        // When & Then: Expecting a BadRequestException due to no changes
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> restaurantService.updateInformations(request, restaurantId))
                .withMessage("No data changes found");
    }

    /**
     * Test case for reserving a table at a restaurant.
     * Verifies that a successful reservation returns the correct message and updates reserved table count.
     */
    @Test
    void reserveTableWillReturnReservationMessage() {
        // Given: An existing restaurant with available tables
        Long restaurantId = 1L;
        Restaurant restaurant = new Restaurant(restaurantId, "Test Restaurant", "123 Main St", 10L, 5L);
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.save(restaurant)).thenReturn(restaurant);

        // When: Reserving a table at the restaurant
        String result = restaurantService.reserveTable(restaurantId);

        // Then: Verify that the reservation message is returned and the reserved table count is updated
        assertThat(result).isEqualTo("Table reserved in the restaurant [Test Restaurant]");
        assertThat(restaurant.getNumberTableReserved()).isEqualTo(6);
    }

    /**
     * Test case for reserving a table when no tables are available.
     * Expects a RestaurantFullException to be thrown.
     */
    @Test
    void reserveTableWillThrowRestaurantFullExceptionWhenNoTablesAvailable() {
        // Given: An existing restaurant with no available tables
        Long restaurantId = 1L;
        Restaurant restaurant = new Restaurant(restaurantId, "Test Restaurant", "123 Main St", 10L, 10L);
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        // When & Then: Expecting a RestaurantFullException when trying to reserve
        assertThatExceptionOfType(RestaurantFullException.class)
                .isThrownBy(() -> restaurantService.reserveTable(restaurantId));
    }

    /**
     * Test case for canceling a table reservation.
     * Verifies that the cancellation returns the correct message and updates reserved table count.
     */
    @Test
    void cancelTableReservationWillReturnCancellationMessage() {
        // Given: An existing restaurant with reserved tables
        Long restaurantId = 1L;
        Restaurant restaurant = new Restaurant(restaurantId, "Test Restaurant", "123 Main St", 10L, 5L);
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(restaurantRepository.save(restaurant)).thenReturn(restaurant);

        // When: Cancelling a table reservation
        String result = restaurantService.cancelTableReservation(restaurantId);

        // Then: Verify the cancellation message and updated reserved table count
        assertThat(result).isEqualTo("Table reservation canceled in the restaurant [Test Restaurant]");
        assertThat(restaurant.getNumberTableReserved()).isEqualTo(4);
    }

    /**
     * Test case for canceling a table reservation when there are no reservations.
     * Expects a BadRequestException to be thrown.
     */
    @Test
    void cancelTableReservationWillThrowBadRequestExceptionWhenNoReservationsToCancel() {
        // Given: An existing restaurant with no reserved tables
        Long restaurantId = 1L;
        Restaurant restaurant = new Restaurant(restaurantId, "Test Restaurant", "123 Main St", 10L, 0L);
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        // When & Then: Expecting a BadRequestException when trying to cancel
        assertThatExceptionOfType(BadRequestException.class)
                .isThrownBy(() -> restaurantService.cancelTableReservation(restaurantId))
                .withMessage("There is no reservations to cancel !!!");
    }

    /**
     * Test case for checking table availability at a restaurant.
     * Verifies that the method returns true when tables are available.
     */
    @Test
    void checkTableAvailabilityWillReturnTrueWhenTablesAvailable() {
        // Given: An existing restaurant with available tables
        Long restaurantId = 1L;
        Restaurant restaurant = new Restaurant(restaurantId, "Test Restaurant", "123 Main St", 10L, 5L);
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        // When: Checking the table availability
        boolean result = restaurantService.checkTableAvailability(restaurantId);

        // Then: Verify that the method returns true for available tables
        assertThat(result).isTrue();
    }

    /**
     * Test case for checking table availability when no tables are available.
     * Expects a RestaurantFullException to be thrown.
     */
    @Test
    void checkTableAvailabilityWillThrowRestaurantFullExceptionWhenNoTablesAvailable() {
        // Given: An existing restaurant with no available tables
        Long restaurantId = 1L;
        Restaurant restaurant = new Restaurant(restaurantId, "Test Restaurant", "123 Main St", 10L, 10L);
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        // When & Then: Expecting a RestaurantFullException when checking availability
        assertThatExceptionOfType(RestaurantFullException.class)
                .isThrownBy(() -> restaurantService.checkTableAvailability(restaurantId));
    }

    /**
     * Test case for checking if a restaurant exists by its ID.
     * Verifies that the method returns true when the restaurant exists.
     */
    @Test
    void restaurantExistByIdWillReturnTrueWhenRestaurantExists() {
        // Given: An existing restaurant ID
        Long restaurantId = 1L;
        Restaurant restaurant = new Restaurant(restaurantId, "Test Restaurant", "123 Main St", 10L, 0L);
        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));

        // When: Checking if the restaurant exists by ID
        boolean result = restaurantService.restaurantExistById(restaurantId);

        // Then: Verify that the method returns true indicating the restaurant exists
        assertThat(result).isTrue();
    }
}