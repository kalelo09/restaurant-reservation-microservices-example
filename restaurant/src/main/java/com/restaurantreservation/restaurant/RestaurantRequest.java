package com.restaurantreservation.restaurant;
/**
 * Request DTO for updating or creating a Restaurant.
 *
 * <p>This record encapsulates the data required to create or update a restaurant in the system.
 * It includes the restaurant's name, address, total number of tables, and the number of tables that are currently reserved.</p>
 *
 * <ul>
 *   <li>The {@code name} field represents the name of the restaurant.</li>
 *   <li>The {@code address} field specifies the location of the restaurant.</li>
 *   <li>The {@code numberTable} field indicates the total number of tables in the restaurant.</li>
 *   <li>The {@code numberTableReserved} field denotes the number of tables currently reserved.</li>
 * </ul>
 */
public record RestaurantRequest(String name, String address, Long numberTable, Long numberTableReserved) {
}
