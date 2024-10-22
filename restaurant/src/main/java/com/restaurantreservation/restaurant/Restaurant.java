package com.restaurantreservation.restaurant;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Entity representing a Restaurant in the reservation system.
 *
 * <p>This class is mapped to the database and contains the restaurant's details,
 * such as name, address, total number of tables, and the number of tables currently reserved.
 * The restaurant name is unique, and all fields have validation constraints.</p>
 *
 * <ul>
 *   <li>The {@code id} is auto-generated using a sequence strategy.</li>
 *   <li>The {@code name} and {@code address} fields cannot be null or empty.</li>
 *   <li>The {@code numberTable} field must be at least 1, representing the total number of tables available in the restaurant.</li>
 *   <li>The {@code numberTableReserved} field cannot be negative and defaults to 0.</li>
 * </ul>
 *
 * <p>Validation annotations ensure that input data meets certain criteria, and Lombok annotations
 * are used for reducing boilerplate code (like getters, setters, equals, and hashCode).</p>
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Entity
public class Restaurant {
    /**
     * Unique identifier for the Restaurant.
     * Generated automatically using a sequence strategy.
     */
    @Id
    @SequenceGenerator(
            name = "restaurant_id_sequence",
            sequenceName = "restaurant_id_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "restaurant_id_sequence"
    )
    private Long id;

    /**
     * The name of the restaurant.
     *
     * <p>This field is required and must not be empty or null. It is also unique, meaning
     * no two restaurants can have the same name.</p>
     */
    @Column(nullable = false, unique = true)
    @NotBlank(message = "Restaurant name must not be empty or null")
    private String name;

    /**
     * The address of the restaurant.
     *
     * <p>This field is required and must not be empty or null.</p>
     */
    @Column(nullable = false)
    @NotBlank(message = "Restaurant address must not be empty or null")
    private String address;

    /**
     * The total number of tables in the restaurant.
     *
     * <p>This field must be at least 1, representing the minimum number of tables
     * a restaurant can have.</p>
     */
    @Min(value = 1, message = "Number of table should be minimum 1")
    private Long numberTable;

    /**
     * The number of tables that are currently reserved.
     *
     * <p>This field cannot be negative and defaults to 0.</p>
     */
    @Min(value = 0, message = "can't have negative number reserved table")
    private Long numberTableReserved = 0L;

}
