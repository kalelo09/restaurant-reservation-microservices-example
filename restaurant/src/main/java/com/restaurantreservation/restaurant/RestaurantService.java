package com.restaurantreservation.restaurant;

import com.restaurantreservation.exception.BadRequestException;
import com.restaurantreservation.exception.DuplicateResourceException;
import com.restaurantreservation.exception.ResourceNotFoundException;
import com.restaurantreservation.exception.RestaurantFullException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;


/**
 * Service class that manages restaurant operations such as registering, updating, reserving,
 * and canceling tables, along with checking restaurant details and availability.
 *
 * <p>This class interacts with the {@link RestaurantRepository} to perform CRUD operations
 * and additional custom logic related to restaurant management. It also includes exception
 * handling for scenarios such as duplicate restaurant names, bad requests, and full capacity.</p>
 *
 * <ul>
 *   <li>{@link #getAllRestaurants(Pageable)}: Fetches a paginated list of all restaurants.</li>
 *   <li>{@link #getRestaurant(Long)}: Retrieves a restaurant by its unique identifier.</li>
 *   <li>{@link #register(RestaurantRequest)}: Registers a new restaurant based on a {@link RestaurantRequest}.</li>
 *   <li>{@link #updateInformations(RestaurantRequest, Long)}: Updates restaurant details such as name, address, or table count.</li>
 *   <li>{@link #reserveTable(Long)}: Reserves a table at the specified restaurant.</li>
 *   <li>{@link #cancelTableReservation(Long)}: Cancels a table reservation at the specified restaurant.</li>
 *   <li>{@link #checkTableAvailability(Long)}: Checks if tables are available for reservation at a restaurant.</li>
 * </ul>
 */
@Slf4j
@Service
public record RestaurantService(RestaurantRepository restaurantRepository) {

    /**
     * Fetches a paginated list of all restaurants.
     *
     * @param pageable Pagination information.
     * @return A page of restaurants.
     */
    public Page<Restaurant> getAllRestaurants(Pageable pageable) {
        log.info("Fetching all restaurants with pagination: {}", pageable);
        return restaurantRepository.findAll(pageable);
    }

    /**
     * Retrieves a restaurant by its unique identifier.
     *
     * @param id The unique identifier of the restaurant.
     * @return The restaurant object.
     * @throws ResourceNotFoundException if the restaurant with the specified ID does not exist.
     */
    public Restaurant getRestaurant(Long id) {
        log.debug("Retrieving restaurant with ID: {}", id);
        return restaurantRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Restaurant with ID [{}] not found", id);
                    return new ResourceNotFoundException("Restaurant with id [%s] not found !!!".formatted(id));
                });
    }

    /**
     * Registers a new restaurant based on a {@link RestaurantRequest}.
     *
     * @param request The request containing restaurant details.
     * @return The registered restaurant object.
     * @throws DuplicateResourceException if the restaurant name is already taken.
     */
    public Restaurant register(RestaurantRequest request) {
        log.info("Registering restaurant with details: {}", request);
        if (restaurantExistByName(request.name())) {
            log.error("Duplicate restaurant name attempt: {}", request.name());
            throw new DuplicateResourceException("Restaurant name already taken !!!");
        }

        Restaurant restaurant = Restaurant.builder()
                .name(request.name())
                .address(request.address())
                .numberTable(request.numberTable())
                .numberTableReserved(request.numberTableReserved())
                .build();
        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        log.info("Successfully registered restaurant: {}", savedRestaurant);
        return savedRestaurant;
    }

    /**
     * Updates restaurant details such as name, address, or table count.
     *
     * @param request     The request containing updated restaurant details.
     * @param restaurantId The unique identifier of the restaurant to update.
     * @return The updated restaurant object.
     * @throws BadRequestException if no changes are found in the update request.
     * @throws DuplicateResourceException if the updated name is already taken.
     */
    public Restaurant updateInformations(RestaurantRequest request, Long restaurantId) {
        log.info("Updating restaurant with ID: {}", restaurantId);
        Restaurant restaurant = getRestaurant(restaurantId);
        boolean changes = false;

        if (request.address() != null && !request.address().equals(restaurant.getAddress())) {
            restaurant.setAddress(request.address());
            changes = true;
            log.debug("Updated address for restaurant ID [{}]: {}", restaurantId, request.address());
        }
        if (request.numberTable() != null && !request.numberTable().equals(restaurant.getNumberTable())) {
            if (request.numberTable() < restaurant.getNumberTableReserved()) {
                log.error("Invalid table number update attempt for restaurant ID [{}]: requested number [{}] is less than reserved [{}]",
                        restaurantId, request.numberTable(), restaurant.getNumberTableReserved());
                throw new IllegalArgumentException("Number of tables should be superior or equal to the number of tables reserved. restaurant got [%s] tables reserved".formatted(restaurant.getNumberTableReserved()));
            }
            restaurant.setNumberTable(request.numberTable());
            changes = true;
            log.debug("Updated number of tables for restaurant ID [{}]: {}", restaurantId, request.numberTable());
        }
        if (request.name() != null && !request.name().equals(restaurant.getName())) {
            if (restaurantExistByName(request.name())) {
                log.error("Duplicate restaurant name update attempt: {}", request.name());
                throw new DuplicateResourceException("Restaurant name already taken !!!");
            }
            restaurant.setName(request.name());
            changes = true;
            log.debug("Updated name for restaurant ID [{}]: {}", restaurantId, request.name());
        }

        if (!changes) {
            log.warn("No data changes found for restaurant ID [{}]", restaurantId);
            throw new BadRequestException("No data changes found");
        }

        Restaurant updatedRestaurant = restaurantRepository.save(restaurant);
        log.info("Successfully updated restaurant: {}", updatedRestaurant);
        return updatedRestaurant;
    }

    /**
     * Reserves a table at the specified restaurant.
     *
     * @param idRestaurant The unique identifier of the restaurant.
     * @return Confirmation message of the reservation.
     * @throws RestaurantFullException if the restaurant is at full capacity.
     */
    public String reserveTable(Long idRestaurant) {
        log.info("Reserving table in restaurant with ID: {}", idRestaurant);
        Restaurant restaurant = getRestaurant(idRestaurant);
        if (!isTablesAvailable(restaurant)) {
            log.error("Attempt to reserve a table in a full restaurant ID [{}]", idRestaurant);
            throw new RestaurantFullException();
        }
        restaurant.setNumberTableReserved(restaurant.getNumberTableReserved() + 1);
        restaurantRepository.save(restaurant);
        String confirmationMessage = "Table reserved in the restaurant [%s]".formatted(restaurant.getName());
        log.info(confirmationMessage);
        return confirmationMessage;
    }

    /**
     * Cancels a table reservation at the specified restaurant.
     *
     * @param idRestaurant The unique identifier of the restaurant.
     * @return Confirmation message of the cancellation.
     * @throws BadRequestException if there are no reservations to cancel.
     */
    public String cancelTableReservation(Long idRestaurant) {
        log.info("Cancelling table reservation in restaurant with ID: {}", idRestaurant);
        Restaurant restaurant = getRestaurant(idRestaurant);
        if (restaurant.getNumberTableReserved() == 0) {
            log.error("Attempt to cancel reservation with none existing reservations in restaurant ID [{}]", idRestaurant);
            throw new BadRequestException("There is no reservations to cancel !!!");
        }
        restaurant.setNumberTableReserved(restaurant.getNumberTableReserved() - 1);
        restaurantRepository.save(restaurant);
        String cancellationMessage = "Table reservation canceled in the restaurant [%s]".formatted(restaurant.getName());
        log.info(cancellationMessage);
        return cancellationMessage;
    }

    /**
     * Checks if tables are available for reservation at a restaurant.
     *
     * @param idRestaurant The unique identifier of the restaurant.
     * @return True if tables are available, otherwise false.
     * @throws RestaurantFullException if the restaurant is at full capacity.
     */
    public boolean checkTableAvailability(Long idRestaurant) {
        log.info("Checking table availability for restaurant with ID: {}", idRestaurant);
        Restaurant restaurant = getRestaurant(idRestaurant);
        if (!isTablesAvailable(restaurant)) {
            log.error("Restaurant ID [{}] is full", idRestaurant);
            throw new RestaurantFullException();
        }
        boolean availability = isTablesAvailable(restaurant);
        log.info("Table availability for restaurant ID [{}]: {}", idRestaurant, availability);
        return availability;
    }

    /**
     * Checks if a restaurant exists by its unique identifier.
     *
     * @param id The unique identifier of the restaurant.
     * @return True if the restaurant exists, otherwise false.
     */
    public boolean restaurantExistById(Long id) {
        log.debug("Checking existence of restaurant with ID: {}", id);
        getRestaurant(id);
        return true;
    }

    /**
     * Checks if a restaurant exists by its name.
     *
     * @param name The name of the restaurant.
     * @return True if the restaurant exists, otherwise false.
     */
    public boolean restaurantExistByName(String name) {
        log.debug("Checking existence of restaurant by name: {}", name);
        return restaurantRepository.existsRestaurantByName(name);
    }

    /**
     * Checks if there are tables available in the specified restaurant.
     *
     * @param restaurant The restaurant to check for availability.
     * @return True if tables are available, otherwise false.
     */
    private boolean isTablesAvailable(Restaurant restaurant) {
        return restaurant.getNumberTableReserved() < restaurant.getNumberTable();
    }
}
