package com.example.dispatch.domain;

import com.example.dispatch.domain.enums.Priority;
import com.example.dispatch.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Immutable Value Object representing an order that has been
 * assigned to a vehicle during dispatch planning.
 *
 * <p>
 * This class is created during dispatch optimization and contains:
 * <ul>
 *     <li>Original order details</li>
 *     <li>Calculated distance from the assigned vehicle</li>
 * </ul>
 *
 * <p>
 * It is NOT a JPA entity.
 * It is a pure domain object used for:
 * <ul>
 *     <li>Dispatch plan generation</li>
 *     <li>Response construction</li>
 *     <li>Business-layer calculations</li>
 * </ul>
 *
 * <p>
 * Immutability:
 * All fields are declared as {@code final} to ensure that once an order
 * is assigned during planning, its state cannot be modified.
 *
 * <p>
 * Distance Calculation:
 * {@code distanceKm} represents the computed geographical distance
 * (in kilometers) between the vehicle and the order location.
 */
@Data
@AllArgsConstructor
public class AssignedOrder {

    /**
     * Unique identifier of the order.
     */
    private final String orderId;

    /**
     * Latitude coordinate of delivery location.
     */
    private final double latitude;

    /**
     * Longitude coordinate of delivery location.
     */
    private final double longitude;

    /**
     * Human-readable delivery address.
     */
    private final String address;

    /**
     * Weight of the package in kilograms.
     */
    private final int packageWeight;

    /**
     * Priority level of the order.
     * Determines dispatch sequencing.
     */
    private final Priority priority;

    /**
     * Calculated distance (in kilometers) from assigned vehicle
     * to delivery location.
     */
    private final double distanceKm;

    /**
     * Convenience constructor to create an AssignedOrder
     * from an existing {@link Order} entity and a computed distance.
     *
     * @param order       Original order entity
     * @param distanceKm  Calculated distance from vehicle to order location
     */
    public AssignedOrder(Order order, double distanceKm) {
        this.orderId = order.getOrderId();
        this.latitude = order.getLatitude();
        this.longitude = order.getLongitude();
        this.address = order.getAddress();
        this.packageWeight = order.getPackageWeight();
        this.priority = order.getPriority();
        this.distanceKm = distanceKm;
    }
}
