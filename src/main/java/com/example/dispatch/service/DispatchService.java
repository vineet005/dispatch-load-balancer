package com.example.dispatch.service;

import com.example.dispatch.domain.DispatchPlan;
import com.example.dispatch.dto.OrderRequest;
import com.example.dispatch.dto.VehicleRequest;

import java.util.List;

/**
 * Core service interface responsible for managing orders and vehicles
 * and generating optimized dispatch plans in the dispatch system.
 * <p>
 * Implementations of this service handle:
 * <ul>
 *     <li>Adding new orders</li>
 *     <li>Adding available vehicles</li>
 *     <li>Generating a dispatch plan based on orders and vehicles</li>
 * </ul>
 * </p>
 *
 * <p>The service typically interacts with repositories, applies business rules,
 * and produces {@link DispatchPlan} objects for downstream processing.</p>
 */
public interface DispatchService {

    /**
     * Adds a list of new orders to the system.
     * <p>
     * Each {@link OrderRequest} contains the details required to create an order.
     * Implementations may validate the requests and persist them to the database.
     * </p>
     *
     * @param requests a list of {@link OrderRequest} objects representing the new orders
     */
    void addOrders(List<OrderRequest> requests);

    /**
     * Adds a list of new vehicles to the system.
     * <p>
     * Each {@link VehicleRequest} contains the details required to create a vehicle entry.
     * Implementations may validate the requests and persist them to the database.
     * </p>
     *
     * @param requests a list of {@link VehicleRequest} objects representing available vehicles
     */
    void addVehicles(List<VehicleRequest> requests);

    /**
     * Generates an optimized dispatch plan.
     * <p>
     * The dispatch plan typically matches orders to vehicles based on
     * availability, capacity, location, and other business rules.
     * </p>
     *
     * @return a list of {@link DispatchPlan} objects representing the assignments of
     *         orders to vehicles
     */
    List<DispatchPlan> generateDispatchPlan();
}
