package com.restaurantreservation.reservation;

import com.restaurantreservation.clients.restaurant.RestaurantClient;
import com.restaurantreservation.exception.BadRequestException;
import com.restaurantreservation.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service class for managing reservation operations.
 *
 * <p>This class is responsible for handling business logic related to reservations, such as
 * registering, updating, canceling, and retrieving reservations. It interacts with the
 * {@link ReservationRepository} to persist reservation data and communicates with the
 * Restaurant service via {@link RestaurantClient} for operations related to restaurant
 * availability and reservation checks.</p>
 *
 * <ul>
 *   <li>{@code getAllReservations} retrieves all reservations with pagination support.</li>
 *   <li>{@code getReservationsNotCanceled} retrieves all active (not canceled) reservations.</li>
 *   <li>{@code getReservationsCanceled} retrieves all canceled reservations.</li>
 *   <li>{@code getReservationsByRestaurant} fetches reservations filtered by restaurant ID.</li>
 *   <li>{@code getReservationsByCustomer} fetches reservations filtered by customer name.</li>
 *   <li>{@code getReservation} retrieves a single reservation by its ID.</li>
 *   <li>{@code register} handles the logic for creating a new reservation, including checking
 *   restaurant existence and availability, and reserving a table.</li>
 *   <li>{@code cancelReservation} cancels an existing reservation and frees up the restaurant table.</li>
 *   <li>{@code updateInformations} allows updating a reservation's details, such as customer name,
 *   restaurant, and reservation date.</li>
 * </ul>
 *
 * <p>Validation, error handling, and interactions with external services are handled within the methods.
 * Custom exceptions like {@link ResourceNotFoundException} and {@link BadRequestException} are thrown
 * for specific error scenarios.</p>
 *
 * <p>Annotations used:</p>
 * <ul>
 *   <li>{@code @Service} - Marks the class as a Spring service component.</li>
 *   <li>{@code @Record} - Makes this class a record, which automatically generates boilerplate code like
 *   constructors, getters, and toString.</li>
 * </ul>
 *
 * @see ReservationRepository
 * @see RestaurantClient
 * @see ResourceNotFoundException
 * @see BadRequestException
 */
@Slf4j
@Service
public record ReservationService(ReservationRepository reservationRepository, RestaurantClient restaurantClient) {

    /**
     * Retrieves all reservations with pagination.
     *
     * @param pageable The pagination information.
     * @return A paginated list of all reservations.
     */
    public Page<Reservation> getAllReservations(Pageable pageable) {
        log.info("Retrieving all reservations with pagination: {}", pageable);
        return reservationRepository.findAll(pageable);
    }

    /**
     * Retrieves all active (not canceled) reservations with pagination.
     *
     * @param pageable The pagination information.
     * @return A paginated list of all active reservations.
     */
    public Page<Reservation> getReservationsNotCanceled(Pageable pageable) {
        log.info("Retrieving not canceled reservations with pagination: {}", pageable);
        return reservationRepository.findAllByCanceledIsFalse(pageable);
    }

    /**
     * Retrieves all canceled reservations with pagination.
     *
     * @param pageable The pagination information.
     * @return A paginated list of all canceled reservations.
     */
    public Page<Reservation> getReservationsCanceled(Pageable pageable) {
        log.info("Retrieving canceled reservations with pagination: {}", pageable);
        return reservationRepository.findAllByCanceledIsTrue(pageable);
    }

    /**
     * Retrieves reservations for a specific restaurant by ID with pagination.
     *
     * @param pageable The pagination information.
     * @param idRestaurant The restaurant ID.
     * @return A paginated list of reservations for the given restaurant.
     */
    public Page<Reservation> getReservationsByRestaurant(Pageable pageable, Long idRestaurant) {
        log.info("Retrieving reservations for restaurant ID {} with pagination: {}", idRestaurant, pageable);
        return reservationRepository.findAllByIdRestaurantEquals(pageable, idRestaurant);
    }

    /**
     * Retrieves reservations by customer name with pagination.
     *
     * @param pageable The pagination information.
     * @param customerName The customer's name.
     * @return A paginated list of reservations for the given customer.
     */
    public Page<Reservation> getReservationsByCustomer(Pageable pageable, String customerName) {
        log.info("Retrieving reservations for customer: {} with pagination: {}", customerName, pageable);
        return reservationRepository.findAllByCustomerNameEquals(pageable, customerName);
    }

    /**
     * Retrieves a reservation by its ID.
     *
     * @param id The reservation ID.
     * @return The reservation object.
     * @throws ResourceNotFoundException if the reservation is not found.
     */
    public Reservation getReservation(Long id) {
        log.info("Retrieving reservation with ID: {}", id);
        return reservationRepository.findById(id)
                .orElseThrow(() -> {
                    String message = "Reservation with id [%s] not found !!!".formatted(id);
                    log.error(message);
                    return new ResourceNotFoundException(message);
                });
    }

    /**
     * Registers a new reservation.
     *
     * <p>This method interacts with the {@link RestaurantClient} to check the existence and availability of the restaurant.
     * It then reserves a table and saves the reservation.</p>
     *
     * @param request The reservation request details.
     * @return The newly created reservation.
     */
    public Reservation register(ReservationRequest request) {
        Long idRestaurant = request.idRestaurant();
        log.info("Registering new reservation for restaurant ID: {}", idRestaurant);

        // Check if the restaurant exists and has availability
        restaurantClient.checkRestaurantExist(idRestaurant);
        restaurantClient.checkAvailability(idRestaurant);

        // Create the reservation object
        Reservation reservation = Reservation.builder()
                .idRestaurant(idRestaurant)
                .customerName(request.customerName())
                .reservationDate(request.reservationDate())
                .build();

        // Reserve a table for the restaurant
        restaurantClient.reserveTable(idRestaurant);
        log.info("Successfully registered reservation: {}", reservation);

        return reservationRepository.save(reservation);
    }

    /**
     * Cancels an existing reservation.
     *
     * <p>This method marks a reservation as canceled and releases the reserved table in the restaurant.</p>
     *
     * @param id The reservation ID.
     * @return The updated reservation after cancellation.
     * @throws BadRequestException if the reservation is already canceled.
     */
    public Reservation cancelReservation(Long id) {
        log.info("Cancelling reservation with ID: {}", id);
        Reservation reservation = getReservation(id);
        if (reservation.isCanceled()) {
            log.warn("Attempted to cancel an already canceled reservation with ID: {}", id);
            throw new BadRequestException("Reservation already canceled !!!");
        }

        // Cancel the table reservation in the restaurant
        restaurantClient.cancelTableReservation(reservation.getIdRestaurant());
        reservation.setCanceled(true);
        log.info("Successfully cancelled reservation: {}", reservation);

        return reservationRepository.save(reservation);
    }

    /**
     * Updates the details of an existing reservation.
     *
     * <p>This method allows updating customer name, restaurant, and reservation date. It interacts with
     * {@link RestaurantClient} to check availability and reserve/cancel tables as needed.</p>
     *
     * @param request The reservation request with updated details.
     * @param reservationId The ID of the reservation to update.
     * @return The updated reservation.
     * @throws BadRequestException if no changes are found in the update request.
     */
    public Reservation updateInformations(ReservationRequest request, Long reservationId) {
        log.info("Updating reservation with ID: {}", reservationId);
        Reservation reservation = getReservation(reservationId);
        boolean changes = false;

        // Update customer name if changed
        if (request.customerName() != null && !request.customerName().equals(reservation.getCustomerName())) {
            reservation.setCustomerName(request.customerName());
            changes = true;
            log.info("Updated customer name for reservation ID {}: {}", reservationId, request.customerName());
        }

        // Update restaurant if changed and check availability
        if (request.idRestaurant() != null && !request.idRestaurant().equals(reservation.getIdRestaurant())) {
            restaurantClient.checkAvailability(request.idRestaurant());
            restaurantClient.reserveTable(request.idRestaurant());
            restaurantClient.cancelTableReservation(reservation.getIdRestaurant());
            reservation.setIdRestaurant(request.idRestaurant());
            changes = true;
            log.info("Updated restaurant for reservation ID {}: {}", reservationId, request.idRestaurant());
        }

        // Update reservation date if changed
        if (request.reservationDate() != null && !request.reservationDate().equals(reservation.getReservationDate())) {
            reservation.setReservationDate(request.reservationDate());
            changes = true;
            log.info("Updated reservation date for reservation ID {}: {}", reservationId, request.reservationDate());
        }

        if (!changes) {
            log.warn("No data changes found for reservation ID {}", reservationId);
            throw new BadRequestException("No data changes found");
        }

        return reservationRepository.save(reservation);
    }
}
