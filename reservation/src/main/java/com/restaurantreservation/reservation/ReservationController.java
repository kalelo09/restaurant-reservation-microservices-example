package com.restaurantreservation.reservation;

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
 * REST controller for managing reservations in the restaurant reservation system.
 *
 * <p>This controller provides endpoints for creating, retrieving, updating, and canceling reservations.
 * It also includes functionality for filtering reservations based on their status and associated restaurant or customer.</p>
 *
 * <ul>
 *   <li>{@code getAllReservations} retrieves a paginated list of all reservations.</li>
 *   <li>{@code getCanceledReservations} retrieves a paginated list of canceled reservations.</li>
 *   <li>{@code getNotCanceledReservations} retrieves a paginated list of reservations that are not canceled.</li>
 *   <li>{@code getReservationsByRestaurant} retrieves reservations for a specific restaurant.</li>
 *   <li>{@code getReservationsByCustomer} retrieves reservations for a specific customer.</li>
 *   <li>{@code getReservationById} retrieves details of a specific reservation by its ID.</li>
 *   <li>{@code registerReservation} registers a new reservation.</li>
 *   <li>{@code cancelReservation} cancels a reservation by its ID.</li>
 *   <li>{@code updateReservation} updates the details of an existing reservation.</li>
 * </ul>
 *
 * <p>All methods are annotated with OpenAPI annotations to generate documentation through Swagger.</p>
 *
 * <p>Validation constraints are applied to input parameters using Jakarta Bean Validation annotations.</p>
 *
 * <p>Logging is implemented using SLF4J to provide detailed insights into operations.</p>
 *
 * @see ReservationService
 * @see GlobalExceptionHandler
 */
@RestController
@AllArgsConstructor
@RequestMapping("api/v1/reservations")
@Tag(name = "Reservation API", description = "API for managing restaurants reservations")
@Import(GlobalExceptionHandler.class)
@Slf4j  // Add logging capability
public class ReservationController {
    private final ReservationService reservationService;

    /**
     * Retrieves a paginated list of all reservations.
     *
     * @param pageable The pagination information (default size is 10, sorted by reservation date).
     * @param pagedResourcesAssembler Assembler for creating paginated resources.
     * @return A {@link PagedModel} containing a list of reservations.
     */
    @GetMapping
    @Operation(summary = "Retrieve list of reservations with pageable and sortable")
    public PagedModel<EntityModel<Reservation>> getAllReservations(@PageableDefault(size = 10, sort = "reservationDate") Pageable pageable,
                                                                   PagedResourcesAssembler<Reservation> pagedResourcesAssembler) {
        log.info("Retrieving all reservations with pageable: {}", pageable);
        Page<Reservation> reservationsPage = reservationService.getAllReservations(pageable);
        log.info("Successfully retrieved {} reservations", reservationsPage.getTotalElements());
        return pagedResourcesAssembler.toModel(reservationsPage, this::toModel);
    }

    /**
     * Retrieves a paginated list of canceled reservations.
     *
     * @param pageable The pagination information (default size is 10, sorted by reservation date).
     * @param pagedResourcesAssembler Assembler for creating paginated resources.
     * @return A {@link PagedModel} containing a list of canceled reservations.
     */
    @GetMapping("/canceled")
    @Operation(summary = "Retrieve list of reservations canceled with pageable and sortable")
    public PagedModel<EntityModel<Reservation>> getCanceledReservations(@PageableDefault(size = 10, sort = "reservationDate") Pageable pageable,
                                                                        PagedResourcesAssembler<Reservation> pagedResourcesAssembler) {
        log.info("Retrieving canceled reservations with pageable: {}", pageable);
        Page<Reservation> reservationsPage = reservationService.getReservationsCanceled(pageable);
        log.info("Successfully retrieved {} canceled reservations", reservationsPage.getTotalElements());
        return pagedResourcesAssembler.toModel(reservationsPage, this::toModel);
    }

    /**
     * Retrieves a paginated list of reservations that are not canceled.
     *
     * @param pageable The pagination information (default size is 10, sorted by reservation date).
     * @param pagedResourcesAssembler Assembler for creating paginated resources.
     * @return A {@link PagedModel} containing a list of reservations that are not canceled.
     */
    @GetMapping("/not_canceled")
    @Operation(summary = "Retrieve list of reservations not canceled with pageable and sortable")
    public PagedModel<EntityModel<Reservation>> getNotCanceledReservations(@PageableDefault(size = 10, sort = "reservationDate") Pageable pageable,
                                                                           PagedResourcesAssembler<Reservation> pagedResourcesAssembler) {
        log.info("Retrieving not canceled reservations with pageable: {}", pageable);
        Page<Reservation> reservationsPage = reservationService.getReservationsNotCanceled(pageable);
        log.info("Successfully retrieved {} not canceled reservations", reservationsPage.getTotalElements());
        return pagedResourcesAssembler.toModel(reservationsPage, this::toModel);
    }

    /**
     * Retrieves a paginated list of reservations for a specific restaurant.
     *
     * @param pageable The pagination information (default size is 10, sorted by reservation date).
     * @param pagedResourcesAssembler Assembler for creating paginated resources.
     * @param idRestaurant The ID of the restaurant for which to retrieve reservations.
     * @return A {@link PagedModel} containing a list of reservations for the specified restaurant.
     */
    @GetMapping("/restaurant/{idRestaurant}")
    @Operation(summary = "Retrieve list of reservations by restaurant with pageable and sortable")
    public PagedModel<EntityModel<Reservation>> getReservationsByRestaurant(@PageableDefault(size = 10, sort = "reservationDate") Pageable pageable,
                                                                            PagedResourcesAssembler<Reservation> pagedResourcesAssembler,
                                                                            @PathVariable Long idRestaurant) {
        log.info("Retrieving reservations for restaurant ID: {} with pageable: {}", idRestaurant, pageable);
        Page<Reservation> reservationsPage = reservationService.getReservationsByRestaurant(pageable, idRestaurant);
        log.info("Successfully retrieved {} reservations for restaurant ID: {}", reservationsPage.getTotalElements(), idRestaurant);
        return pagedResourcesAssembler.toModel(reservationsPage, this::toModel);
    }

    /**
     * Retrieves a paginated list of reservations for a specific customer.
     *
     * @param pageable The pagination information (default size is 10, sorted by reservation date).
     * @param pagedResourcesAssembler Assembler for creating paginated resources.
     * @param customerName The name of the customer for whom to retrieve reservations.
     * @return A {@link PagedModel} containing a list of reservations for the specified customer.
     */
    @GetMapping("/customer/{customerName}")
    @Operation(summary = "Retrieve list of reservations by customer with pageable and sortable")
    public PagedModel<EntityModel<Reservation>> getReservationsByCustomer(@PageableDefault(size = 10, sort = "reservationDate") Pageable pageable,
                                                                          PagedResourcesAssembler<Reservation> pagedResourcesAssembler,
                                                                          @PathVariable String customerName) {
        log.info("Retrieving reservations for customer: {} with pageable: {}", customerName, pageable);
        Page<Reservation> reservationsPage = reservationService.getReservationsByCustomer(pageable, customerName);
        log.info("Successfully retrieved {} reservations for customer: {}", reservationsPage.getTotalElements(), customerName);
        return pagedResourcesAssembler.toModel(reservationsPage, this::toModel);
    }

    /**
     * Retrieves details of a specific reservation by its ID.
     *
     * @param id The ID of the reservation to retrieve.
     * @return An {@link EntityModel} containing the reservation details.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Retrieve details of a specific Reservation by ID")
    public EntityModel<Reservation> getReservationById(@PathVariable Long id) {
        log.info("Retrieving reservation with ID: {}", id);
        Reservation reservation = reservationService.getReservation(id);
        log.info("Successfully retrieved reservation: {}", reservation);
        return toModel(reservation);
    }

    /**
     * Registers a new reservation with the provided details.
     *
     * @param reservation The details of the reservation to register.
     * @return An {@link EntityModel} containing the registered reservation details.
     */
    @PostMapping
    @Operation(summary = "Register new reservation")
    public ResponseEntity<EntityModel<Reservation>> registerReservation(@Valid @RequestBody ReservationRequest reservation) {
        log.info("Registering new reservation: {}", reservation);
        EntityModel<Reservation> response = toModel(reservationService.register(reservation));
        log.info("Successfully registered reservation: {}", response.getContent());
        return ResponseEntity.ok(response);
    }

    /**
     * Cancels a reservation by its ID.
     *
     * @param idReservation The ID of the reservation to cancel.
     * @return An {@link EntityModel} containing the canceled reservation details.
     */
    @PutMapping("/cancel/{idReservation}")
    @Operation(summary = "Cancel a reservation")
    public ResponseEntity<EntityModel<Reservation>> cancelReservation(@PathVariable Long idReservation) {
        log.info("Cancelling reservation with ID: {}", idReservation);
        EntityModel<Reservation> response = toModel(reservationService.cancelReservation(idReservation));
        log.info("Successfully cancelled reservation: {}", response.getContent());
        return ResponseEntity.ok(response);
    }

    /**
     * Updates the details of an existing reservation.
     *
     * @param idReservation The ID of the reservation to update.
     * @param request The new details of the reservation.
     * @return An {@link EntityModel} containing the updated reservation details.
     */
    @PutMapping("/{idReservation}")
    @Operation(summary = "Update a reservation")
    public ResponseEntity<EntityModel<Reservation>> updateReservation(@PathVariable Long idReservation, @Valid @RequestBody ReservationRequest request) {
        log.info("Updating reservation with ID: {} with new details: {}", idReservation, request);
        EntityModel<Reservation> response = toModel(reservationService.updateInformations(request, idReservation));
        log.info("Successfully updated reservation: {}", response.getContent());
        return ResponseEntity.ok(response);
    }

    /**
     * Converts a {@link Reservation} object to an {@link EntityModel} with links.
     *
     * @param reservation The reservation to convert.
     * @return An {@link EntityModel} containing the reservation and its related links.
     */
    private EntityModel<Reservation> toModel(Reservation reservation) {
        EntityModel<Reservation> model = EntityModel.of(reservation);
        Link selfLink = linkTo(methodOn(ReservationController.class).getReservationById(reservation.getId())).withSelfRel();
        Link updateLink = linkTo(methodOn(ReservationController.class).updateReservation(reservation.getId(), null)).withRel("update");
        Link cancelReservationLink = linkTo(methodOn(ReservationController.class).cancelReservation(reservation.getId())).withRel("cancel");
        model.add(selfLink, updateLink, cancelReservationLink);
        return model;
    }
}
