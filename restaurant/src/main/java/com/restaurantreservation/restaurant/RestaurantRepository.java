package com.restaurantreservation.restaurant;

import org.springframework.data.jpa.repository.JpaRepository;


/**
 * Repository interface for performing CRUD operations on the {@link Restaurant} entity.
 *
 * <p>This interface extends {@link JpaRepository}, providing standard methods to interact with the
 * restaurant database (e.g., save, find, delete). It also contains custom query methods for specific
 * use cases related to the restaurant entity.</p>
 *
 * <ul>
 *   <li>{@link #existsRestaurantByName(String)}: Checks if a restaurant with the given name exists.</li>
 * </ul>
 *
 * <p>By extending {@code JpaRepository}, this repository automatically inherits several useful methods for
 * interacting with the database without needing to write boilerplate code.</p>
 *
 * @see Restaurant
 * @see JpaRepository
 */
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    /**
     * Checks whether a restaurant with the specified name exists in the database.
     *
     * @param name the name of the restaurant to check for existence
     * @return {@code true} if a restaurant with the given name exists, {@code false} otherwise
     */
    boolean existsRestaurantByName(String name);
}
