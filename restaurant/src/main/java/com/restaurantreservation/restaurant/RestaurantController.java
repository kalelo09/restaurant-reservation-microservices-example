package com.restaurantreservation.restaurant;

import com.restaurantreservation.exception.GlobalExceptionHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * REST controller for managing restaurants in the reservation system.
 *
 * <p>This controller provides endpoints for creating, retrieving, updating, and deleting restaurants.
 * It also includes functionality for checking restaurant existence and table availability.</p>
 *
 * <ul>
 *   <li>{@code getAllRestaurants} retrieves a paginated list of all restaurants.</li>
 *   <li>{@code getRestaurantById} retrieves details of a specific restaurant by its ID.</li>
 *   <li>{@code reserveTable} reserves a table in a specified restaurant.</li>
 *   <li>{@code cancelTableReservation} cancels a reservation in a specified restaurant.</li>
 *   <li>{@code checkRestaurantExist} checks whether a restaurant exists by its ID.</li>
 *   <li>{@code checkAvailability} checks if there are available tables in a restaurant.</li>
 *   <li>{@code registerRestaurant} registers a new restaurant.</li>
 *   <li>{@code updateRestaurant} updates the details of an existing restaurant.</li>
 * </ul>
 *
 * <p>All methods are annotated with OpenAPI annotations to generate documentation through Swagger.</p>
 *
 * <p>Validation constraints are applied to input parameters using Jakarta Bean Validation annotations.</p>
 *
 * <p>Logging is implemented using SLF4J to provide detailed insights into operations.</p>
 *
 * @see RestaurantService
 * @see GlobalExceptionHandler
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("api/v1/restaurants")
@Tag(name = "Restaurant API", description = "API for managing restaurants")
@Import(GlobalExceptionHandler.class)
public class RestaurantController {

    private final RestaurantService restaurantService;

    /**
     * Retrieves a paginated list of all restaurants.
     *
     * @param pageable The pagination information (default size is 10, sorted by name).
     * @param pagedResourcesAssembler Assembler for creating paginated resources.
     * @return A {@link PagedModel} containing a list of restaurants.
     */
    @GetMapping
    @Operation(summary = "Retrieve list of restaurants with pageable and sortable")
    public PagedModel<EntityModel<Restaurant>> getAllRestaurants(@PageableDefault(size = 10, sort = "name") Pageable pageable,
                                                                 PagedResourcesAssembler<Restaurant> pagedResourcesAssembler) {
        log.info("Fetching all restaurants with pagination: {}", pageable);
        Page<Restaurant> restaurantsPage = restaurantService.getAllRestaurants(pageable);
        PagedModel<EntityModel<Restaurant>> model = pagedResourcesAssembler.toModel(restaurantsPage, this::toModel);
        log.info("Fetched {} restaurants", restaurantsPage.getTotalElements());
        return model;
    }

    /**
     * Retrieves details of a specific restaurant by its ID.
     *
     * @param id The ID of the restaurant to retrieve.
     * @return An {@link EntityModel} containing the restaurant details.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Retrieve details of a specific Restaurant by ID")
    public EntityModel<Restaurant> getRestaurantById(@PathVariable Long id) {
        log.debug("Retrieving restaurant with ID: {}", id);
        Restaurant restaurant = restaurantService.getRestaurant(id);
        log.info("Retrieved restaurant: {}", restaurant);
        return toModel(restaurant);
    }

    /**
     * Reserves a table in a specified restaurant.
     *
     * @param restaurantId The ID of the restaurant where the table is to be reserved.
     * @return A confirmation message indicating the reservation status.
     */
    @PutMapping("/reservation/{restaurantId}")
    @Operation(summary = "Reserve table in a restaurant")
    public ResponseEntity<String> reserveTable(@PathVariable("restaurantId") Long restaurantId) {
        log.info("Reserving table in restaurant with ID: {}", restaurantId);
        String confirmationMessage = restaurantService.reserveTable(restaurantId);
        log.info(confirmationMessage);
        return ResponseEntity.ok(confirmationMessage);
    }

    /**
     * Cancels a table reservation in a specified restaurant.
     *
     * @param restaurantId The ID of the restaurant where the reservation is to be canceled.
     * @return A cancellation message indicating the status of the operation.
     */
    @PutMapping("/cancel_reservation/{restaurantId}")
    @Operation(summary = "Cancel reservation table in a restaurant")
    public ResponseEntity<String> cancelTableReservation(@PathVariable("restaurantId") Long restaurantId) {
        log.info("Cancelling table reservation in restaurant with ID: {}", restaurantId);
        String cancellationMessage = restaurantService.cancelTableReservation(restaurantId);
        log.info(cancellationMessage);
        return ResponseEntity.ok(cancellationMessage);
    }

    /**
     * Checks whether a restaurant exists by its ID.
     *
     * @param restaurantId The ID of the restaurant to check.
     * @return A boolean indicating the existence of the restaurant.
     */
    @GetMapping("/existence/{restaurantId}")
    @Operation(summary = "Check the existence of a restaurant")
    public ResponseEntity<Boolean> checkRestaurantExist(@PathVariable("restaurantId") Long restaurantId) {
        log.debug("Checking existence of restaurant with ID: {}", restaurantId);
        boolean exists = restaurantService.restaurantExistById(restaurantId);
        log.info("Restaurant ID [{}] exists: {}", restaurantId, exists);
        return ResponseEntity.ok(exists);
    }

    /**
     * Checks if there are available tables in a restaurant.
     *
     * @param restaurantId The ID of the restaurant to check for availability.
     * @return A boolean indicating the availability of tables.
     */
    @GetMapping("/check_availability/{restaurantId}")
    @Operation(summary = "Check availability of table in restaurant")
    public ResponseEntity<Boolean> checkAvailability(@PathVariable("restaurantId") Long restaurantId) {
        log.info("Checking availability of tables in restaurant with ID: {}", restaurantId);
        boolean available = restaurantService.checkTableAvailability(restaurantId);
        log.info("Table availability for restaurant ID [{}]: {}", restaurantId, available);
        return ResponseEntity.ok(available);
    }

    /**
     * Registers a new restaurant with the provided details.
     *
     * @param restaurant The details of the restaurant to register.
     * @return An {@link EntityModel} containing the registered restaurant details.
     */
    @PostMapping
    @Operation(summary = "Register new restaurant")
    public ResponseEntity<EntityModel<Restaurant>> registerRestaurant(@Valid @RequestBody RestaurantRequest restaurant) {
        log.info("Registering new restaurant with details: {}", restaurant);
        EntityModel<Restaurant> response = toModel(restaurantService.register(restaurant));
        log.info("Successfully registered restaurant: {}", restaurant);
        return ResponseEntity.ok(response);
    }

    /**
     * Updates the details of an existing restaurant.
     *
     * @param restaurantId The ID of the restaurant to update.
     * @param restaurantRequest The new details of the restaurant.
     * @return An {@link EntityModel} containing the updated restaurant details.
     */
    @PutMapping("{restaurantId}")
    @Operation(summary = "Update informations of a restaurant")
    public ResponseEntity<EntityModel<Restaurant>> updateRestaurant(@PathVariable("restaurantId") Long restaurantId,
                                                                    @RequestBody @Valid RestaurantRequest restaurantRequest) {
        log.info("Updating restaurant with ID: {} with details: {}", restaurantId, restaurantRequest);
        EntityModel<Restaurant> response = toModel(restaurantService.updateInformations(restaurantRequest, restaurantId));
        log.info("Successfully updated restaurant ID [{}]", restaurantId);
        return ResponseEntity.ok(response);
    }

    /**
     * Converts a {@link Restaurant} object to an {@link EntityModel} with links.
     *
     * @param restaurant The restaurant to convert.
     * @return An {@link EntityModel} containing the restaurant and its related links.
     */
    private EntityModel<Restaurant> toModel(Restaurant restaurant) {
        EntityModel<Restaurant> model = EntityModel.of(restaurant);
        // Self link (detail of restaurant)
        Link selfLink = linkTo(methodOn(RestaurantController.class).getRestaurantById(restaurant.getId())).withSelfRel();
        // Update link (for updating the restaurant)
        Link updateLink = linkTo(methodOn(RestaurantController.class)
                .updateRestaurant(restaurant.getId(), null))  // null for the body in link generation
                .withRel("update");
        // Check availability (for reserving table)
        Link checkAvailabilityLink = linkTo(methodOn(RestaurantController.class)
                .checkAvailability(restaurant.getId()))  // null for the body in link generation
                .withRel("checkAvailability");

        model.add(selfLink, updateLink, checkAvailabilityLink);
        return model;
    }
}
