package com.example.dispatch.controller;

import com.example.dispatch.domain.DispatchPlan;
import com.example.dispatch.dto.*;
import com.example.dispatch.service.DispatchService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller responsible for handling all dispatch-related API requests.
 *
 * <p>
 * Responsibilities:
 * <ul>
 *     <li>Accept delivery orders</li>
 *     <li>Accept vehicle details</li>
 *     <li>Expose optimized dispatch plan</li>
 * </ul>
 *
 * <p>
 * Design Principles:
 * <ul>
 *     <li>Thin Controller – business logic delegated to service layer</li>
 *     <li>Validation handled via Jakarta Bean Validation</li>
 *     <li>HTTP semantics respected (201 for creation)</li>
 * </ul>
 *
 * <p>
 * Base URL: /api/dispatch
 */
@RestController
@RequestMapping("/api/dispatch")
@RequiredArgsConstructor
public class DispatchController {

    /**
     * Service layer dependency.
     * All business logic is delegated here to maintain
     * separation of concerns.
     */
    private final DispatchService dispatchService;

    /**
     * Accepts and stores delivery orders.
     *
     * <p>
     * Validation Rules:
     * <ul>
     *     <li>Request body must not be empty</li>
     *     <li>Each OrderRequest must pass field-level validation</li>
     * </ul>
     *
     * <p>
     * HTTP 201 (Created) is returned upon successful persistence.
     *
     * @param requests List of validated order requests
     * @return ApiResponse indicating operation status
     */
    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse addOrders(
            @RequestBody
            @Valid
            @NotEmpty(message = "Order list cannot be empty")
            List<@Valid OrderRequest> requests) {

        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("Order list cannot be empty");
        }

        // Delegating to service layer
        dispatchService.addOrders(requests);

        return new ApiResponse("Delivery orders accepted.", "success");
    }

    /**
     * Accepts and stores vehicle details.
     *
     * <p>
     * Validation Rules:
     * <ul>
     *     <li>Request body must not be empty</li>
     *     <li>Each VehicleRequest must pass field-level validation</li>
     * </ul>
     *
     * <p>
     * HTTP 201 (Created) is returned upon successful persistence.
     *
     * @param requests List of validated vehicle requests
     * @return ApiResponse indicating operation status
     */
    @PostMapping("/vehicles")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse addVehicles(
            @RequestBody
            @Valid
            @NotEmpty(message = "Vehicle list cannot be empty")
            List<@Valid VehicleRequest> requests) {

        // Delegating to service layer
        dispatchService.addVehicles(requests);

        return new ApiResponse("Vehicle details accepted.", "success");
    }

    /**
     * Retrieves optimized dispatch plan for all vehicles.
     *
     * <p>
     * The dispatch algorithm:
     * <ul>
     *     <li>Respects vehicle capacity constraints</li>
     *     <li>Prioritizes higher priority orders</li>
     *     <li>Optimizes based on distance calculation</li>
     * </ul>
     *
     * @return List of DispatchPlan aggregates
     */
    @GetMapping("/plan")
    public List<DispatchPlan> getDispatchPlan() {

        // Service layer performs sorting, assignment and optimization
        return dispatchService.generateDispatchPlan();
    }
}
