package com.restaurantreservation.reservation;

import java.time.LocalDate;

/**
 * A request object for creating or updating a Reservation in the restaurant reservation system.
 *
 * <p>This record is used as a data transfer object (DTO) to encapsulate the details
 * required to make a reservation, such as the restaurant ID, customer name, and reservation date.</p>
 *
 * <ul>
 *   <li>The {@code idRestaurant} field represents the ID of the restaurant where the reservation will be made and cannot be null.</li>
 *   <li>The {@code customerName} field holds the name of the customer making the reservation and cannot be null.</li>
 *   <li>The {@code reservationDate} field stores the date for which the reservation is requested and cannot be null.</li>
 * </ul>
 *
 */

public record ReservationRequest(Long idRestaurant, String customerName, LocalDate reservationDate) {
}
