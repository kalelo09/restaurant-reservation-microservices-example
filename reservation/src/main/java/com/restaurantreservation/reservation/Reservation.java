package com.restaurantreservation.reservation;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Entity representing a Reservation in the restaurant reservation system.
 *
 * <p>This class is mapped to the database and contains the reservation details,
 * such as the associated restaurant ID, customer name, reservation date, and cancellation status.</p>
 *
 * <ul>
 *   <li>The {@code id} is auto-generated using a sequence strategy.</li>
 *   <li>The {@code idRestaurant} is the ID of the restaurant where the reservation is made, and it cannot be null.</li>
 *   <li>The {@code customerName} field holds the name of the customer making the reservation, and it cannot be null.</li>
 *   <li>The {@code reservationDate} field stores the date for the reservation, and it cannot be null.</li>
 *   <li>The {@code canceled} field indicates if the reservation has been canceled, and it defaults to {@code false}.</li>
 * </ul>
 *
 * <p>Lombok annotations are used to reduce boilerplate code by generating
 * common methods such as getters, setters, and constructors.</p>
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Entity
public class Reservation {

    /**
     * Unique identifier for the Reservation.
     * Generated automatically using a sequence strategy.
     */
    @Id
    @SequenceGenerator(
            name = "reservation_id_sequence",
            sequenceName = "reservation_id_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "reservation_id_sequence"
    )
    private Long id;

    /**
     * ID of the restaurant where the reservation is made.
     *
     * <p>This field is required and cannot be null.</p>
     */
    @Column(nullable = false)
    private Long idRestaurant;

    /**
     * Name of the customer making the reservation.
     *
     * <p>This field is required and cannot be null.</p>
     */
    @Column(nullable = false)
    private String customerName;

    /**
     * Date of the reservation.
     *
     * <p>This field is required and cannot be null. It stores the specific date
     * for which the reservation has been made.</p>
     */
    @Column(nullable = false)
    private LocalDate reservationDate;

    /**
     * A boolean flag indicating whether the reservation has been canceled.
     *
     * <p>Defaults to {@code false}, meaning the reservation is active. If set to {@code true},
     * it marks the reservation as canceled.</p>
     */
    @Column(columnDefinition = "boolean default false")
    private boolean canceled = false;
}
