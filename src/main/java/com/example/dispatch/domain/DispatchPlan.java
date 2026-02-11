package com.example.dispatch.domain;

import com.example.dispatch.entity.Order;
import com.example.dispatch.entity.Vehicle;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Aggregate Root representing a dispatch plan for a single vehicle.
 *
 * <p>
 * In Domain-Driven Design (DDD), this class acts as an Aggregate Root
 * that maintains consistency boundaries for:
 * <ul>
 *     <li>Vehicle capacity constraints</li>
 *     <li>Order assignment logic</li>
 *     <li>Total load calculation</li>
 *     <li>Total distance calculation</li>
 * </ul>
 *
 * <p>
 * Responsibilities:
 * <ul>
 *     <li>Ensure vehicle capacity is never exceeded</li>
 *     <li>Maintain list of assigned orders</li>
 *     <li>Track cumulative load and distance</li>
 * </ul>
 *
 * <p>
 * Important Design Notes:
 * <ul>
 *     <li>Encapsulates all mutation logic inside domain layer</li>
 *     <li>Exposes read-only view of assigned orders</li>
 *     <li>Protects invariants via controlled methods</li>
 * </ul>
 */
@Getter
public class DispatchPlan {

    /**
     * Vehicle for which this dispatch plan is created.
     * Immutable once plan is initialized.
     */
    private final Vehicle vehicle;

    /**
     * Internal mutable list of assigned orders.
     * Exposed externally as an unmodifiable list.
     */
    private final List<AssignedOrder> assignedOrders = new ArrayList<>();

    /**
     * Total weight (in kg) currently assigned to the vehicle.
     */
    private int totalLoad;

    /**
     * Total distance (in km) accumulated for assigned deliveries.
     */
    private double totalDistance;

    /**
     * Creates a dispatch plan for a specific vehicle.
     *
     * @param vehicle Vehicle for which dispatch planning is performed
     */
    public DispatchPlan(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    /**
     * Checks whether the vehicle can accommodate additional weight
     * without violating its capacity constraint.
     *
     * @param weight Package weight to evaluate
     * @return true if capacity allows assignment, false otherwise
     */
    public boolean canAccommodate(int weight) {
        return totalLoad + weight <= vehicle.getCapacity();
    }

    /**
     * Assigns an order to this dispatch plan.
     *
     * <p>
     * Business Rules Enforced:
     * <ul>
     *     <li>Vehicle capacity must not be exceeded</li>
     *     <li>Total load must be updated</li>
     *     <li>Total distance must be updated</li>
     * </ul>
     *
     * @param order      Order to assign
     * @param distanceKm Pre-calculated distance between vehicle and order
     *
     * @throws IllegalStateException if vehicle capacity would be exceeded
     */
    public void assignOrder(Order order, double distanceKm) {

        // Validate capacity constraint before assignment
        if (!canAccommodate(order.getPackageWeight())) {
            throw new IllegalStateException(
                    "Vehicle capacity exceeded for vehicle: " + vehicle.getVehicleId()
            );
        }

        // Create immutable AssignedOrder value object
        assignedOrders.add(new AssignedOrder(order, distanceKm));

        // Update aggregate state
        totalLoad += order.getPackageWeight();
        totalDistance += distanceKm;
    }

    /**
     * Returns a read-only view of assigned orders.
     *
     * <p>
     * This prevents external layers from modifying the internal list,
     * preserving aggregate integrity.
     *
     * @return Unmodifiable list of assigned orders
     */
    public List<AssignedOrder> getAssignedOrders() {
        return Collections.unmodifiableList(assignedOrders);
    }
}
