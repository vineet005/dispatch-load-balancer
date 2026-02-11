package com.example.dispatch.service.impl;

import com.example.dispatch.domain.DispatchPlan;
import com.example.dispatch.entity.Order;
import com.example.dispatch.entity.Vehicle;
import com.example.dispatch.dto.OrderRequest;
import com.example.dispatch.dto.VehicleRequest;
import com.example.dispatch.exception.ValidationException;
import com.example.dispatch.repository.OrderRepository;
import com.example.dispatch.repository.VehicleRepository;
import com.example.dispatch.service.DispatchService;
import com.example.dispatch.strategy.HaversineDistanceStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Default implementation of {@link DispatchService}.
 * <p>
 * This service is responsible for managing orders and vehicles and generating optimized
 * dispatch plans. It assigns orders to vehicles based on proximity and vehicle capacity.
 * </p>
 *
 * <h3>Algorithm for generating dispatch plans:</h3>
 * <ol>
 *   <li>Sort orders by priority: HIGH → MEDIUM → LOW</li>
 *   <li>For each order, find the nearest vehicle that has enough available capacity</li>
 *   <li>Assign the order to the vehicle that minimizes distance (greedy approach)</li>
 *   <li>If no suitable vehicle is found, skip the order gracefully</li>
 * </ol>
 *
 * <p>This implementation relies on {@link HaversineDistanceStrategy} to calculate
 * distances between vehicle and order locations.</p>
 */
@Service
@RequiredArgsConstructor
public class DispatchServiceImpl implements DispatchService {

    private final OrderRepository orderRepository;
    private final VehicleRepository vehicleRepository;
    private final HaversineDistanceStrategy distanceStrategy;

    /**
     * Generates an optimized dispatch plan.
     * <p>
     * Orders are assigned to vehicles based on priority and nearest distance while
     * respecting vehicle capacity. Unassignable orders (due to capacity limits) are
     * skipped without failing the process.
     * </p>
     *
     * @return a list of {@link DispatchPlan} representing the assignment of orders to vehicles
     */
    @Override
    public List<DispatchPlan> generateDispatchPlan() {

        List<Order> orders = orderRepository.findAll();
        List<Vehicle> vehicles = vehicleRepository.findAll();

        if (orders.isEmpty() || vehicles.isEmpty()) {
            return Collections.emptyList();
        }

        // Sort orders by priority: HIGH → MEDIUM → LOW
        orders.sort(Comparator.comparing(Order::getPriority));

        // Initialize dispatch plans for each vehicle
        Map<String, DispatchPlan> dispatchPlanMap = new LinkedHashMap<>();
        for (Vehicle vehicle : vehicles) {
            dispatchPlanMap.put(vehicle.getVehicleId(), new DispatchPlan(vehicle));
        }

        // Assign orders
        for (Order order : orders) {
            DispatchPlan bestPlan = null;
            double shortestDistance = Double.MAX_VALUE;

            for (DispatchPlan plan : dispatchPlanMap.values()) {

                if (!plan.canAccommodate(order.getPackageWeight())) {
                    continue;
                }

                Vehicle vehicle = plan.getVehicle();
                double distance = distanceStrategy.calculate(
                        vehicle.getCurrentLatitude(),
                        vehicle.getCurrentLongitude(),
                        order.getLatitude(),
                        order.getLongitude()
                );

                if (distance < shortestDistance) {
                    shortestDistance = distance;
                    bestPlan = plan;
                }
            }

            // Assign order if a suitable vehicle is found
            if (bestPlan != null) {
                bestPlan.assignOrder(order, shortestDistance);
            }
            // else → order is unassignable (capacity exhausted), skip gracefully
        }

        return new ArrayList<>(dispatchPlanMap.values());
    }

    /**
     * Adds a list of new orders to the system.
     * <p>
     * Validates that the request list is not empty and that no order ID already exists.
     * Persists the orders using {@link OrderRepository#saveAll}.
     * </p>
     *
     * @param requests the list of {@link OrderRequest} to add
     * @throws ValidationException if the list is empty or any order ID already exists
     */
    @Override
    public void addOrders(List<OrderRequest> requests) {

        if (requests == null || requests.isEmpty()) {
            throw new ValidationException("Order list cannot be empty");
        }

        for (OrderRequest req : requests) {
            if (orderRepository.existsByOrderId(req.getOrderId())) {
                throw new ValidationException(
                        "Order with ID " + req.getOrderId() + " already exists"
                );
            }
        }

        List<Order> orders = requests.stream()
                .map(this::mapToOrder)
                .toList();

        orderRepository.saveAll(orders);
    }

    /**
     * Adds a list of new vehicles to the system.
     * <p>
     * Validates that the request list is not empty and that no vehicle ID already exists.
     * Persists the vehicles using {@link VehicleRepository#saveAll}.
     * </p>
     *
     * @param requests the list of {@link VehicleRequest} to add
     * @throws ValidationException if the list is empty or any vehicle ID already exists
     */
    @Override
    public void addVehicles(List<VehicleRequest> requests) {

        if (requests == null || requests.isEmpty()) {
            throw new ValidationException("Vehicle list cannot be empty");
        }

        for (VehicleRequest req : requests) {
            if (vehicleRepository.existsByVehicleId(req.getVehicleId())) {
                throw new ValidationException(
                        "Vehicle with ID " + req.getVehicleId() + " already exists"
                );
            }
        }

        List<Vehicle> vehicles = requests.stream()
                .map(this::mapToVehicle)
                .toList();

        vehicleRepository.saveAll(vehicles);
    }

    /**
     * Maps an {@link OrderRequest} DTO to an {@link Order} entity.
     *
     * @param req the order request DTO
     * @return the mapped {@link Order} entity
     */
    private Order mapToOrder(OrderRequest req) {
        return new Order(
                req.getOrderId(),
                req.getLatitude(),
                req.getLongitude(),
                req.getAddress(),
                req.getPackageWeight(),
                req.getPriority()
        );
    }

    /**
     * Maps a {@link VehicleRequest} DTO to a {@link Vehicle} entity.
     *
     * @param req the vehicle request DTO
     * @return the mapped {@link Vehicle} entity
     */
    private Vehicle mapToVehicle(VehicleRequest req) {
        return new Vehicle(
                req.getVehicleId(),
                req.getCapacity(),
                req.getCurrentLatitude(),
                req.getCurrentLongitude(),
                req.getCurrentAddress()
        );
    }
}
