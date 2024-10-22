package com.restaurantreservation.reservation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link Reservation} entities.
 *
 * <p>This interface extends {@link JpaRepository}, providing CRUD operations for the Reservation entity.
 * Additionally, it defines custom query methods for retrieving paginated results based on different reservation
 * statuses and filters, such as whether a reservation is canceled, filtering by restaurant ID, or filtering by customer name.</p>
 *
 * <p>Spring Data JPA automatically provides implementations for these methods based on method name conventions,
 * meaning that complex queries can be executed without manually writing SQL or JPQL.</p>
 *
 * <p>Key features include:
 * <ul>
 *   <li>Pagination support through the {@link Pageable} parameter, ensuring efficient retrieval of data when handling large datasets.</li>
 *   <li>Custom query methods to filter reservations based on their cancellation status, restaurant ID, and customer name.</li>
 * </ul>
 *
 * <p>The repository provides the following query methods:
 * <ul>
 *   <li>{@code findAllByCanceledIsTrue(Pageable pageable)}: Finds all reservations that are canceled.</li>
 *   <li>{@code findAllByCanceledIsFalse(Pageable pageable)}: Finds all reservations that are not canceled.</li>
 *   <li>{@code findAllByIdRestaurantEquals(Pageable pageable, Long idRestaurant)}: Finds all reservations for a specific restaurant.</li>
 *   <li>{@code findAllByCustomerNameEquals(Pageable pageable, String customerName)}: Finds all reservations made by a specific customer.</li>
 * </ul>
 * </p>
 *
 * @see Reservation
 * @see JpaRepository
 */
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    /**
     * Retrieves all canceled reservations in a paginated format.
     *
     * @param pageable the pagination information, which contains page number, size, and sorting details.
     * @return a page of reservations where the {@code canceled} field is {@code true}.
     */
    public Page<Reservation> findAllByCanceledIsTrue(Pageable pageable);

    /**
     * Retrieves all active (non-canceled) reservations in a paginated format.
     *
     * @param pageable the pagination information, which contains page number, size, and sorting details.
     * @return a page of reservations where the {@code canceled} field is {@code false}.
     */
    public Page<Reservation> findAllByCanceledIsFalse(Pageable pageable);

    /**
     * Retrieves all reservations for a specific restaurant in a paginated format.
     *
     * @param pageable the pagination information, which contains page number, size, and sorting details.
     * @param idRestaurant the ID of the restaurant to filter reservations.
     * @return a page of reservations for the given restaurant.
     */
    public Page<Reservation> findAllByIdRestaurantEquals(Pageable pageable, Long idRestaurant);

    /**
     * Retrieves all reservations made by a specific customer in a paginated format.
     *
     * @param pageable the pagination information, which contains page number, size, and sorting details.
     * @param customerName the name of the customer to filter reservations.
     * @return a page of reservations made by the specified customer.
     */
    public Page<Reservation> findAllByCustomerNameEquals(Pageable pageable, String customerName);
}
