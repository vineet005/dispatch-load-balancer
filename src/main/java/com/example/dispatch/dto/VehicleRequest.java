package com.example.dispatch.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO representing vehicle information submitted via API.
 *
 * <p>
 * This class defines the external API contract for vehicle registration.
 * It is validated at the controller boundary using Jakarta Bean Validation.
 *
 * <p>
 * Validation Strategy:
 * <ul>
 *     <li>Vehicle ID must not be blank</li>
 *     <li>Capacity must be positive</li>
 *     <li>Latitude must be within [-90, 90]</li>
 *     <li>Longitude must be within [-180, 180]</li>
 *     <li>Current address must not be blank</li>
 * </ul>
 *
 * <p>
 * Important:
 * This is NOT a JPA entity.
 * It is a pure input model used for validation and decoupling
 * the API layer from the persistence layer.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VehicleRequest {

    /**
     * Unique identifier of the vehicle.
     * Must not be null or blank.
     */
    @NotBlank(message = "Vehicle ID is required")
    private String vehicleId;

    /**
     * Maximum carrying capacity of the vehicle (in kilograms).
     * Must be a positive number.
     */
    @NotNull(message = "Capacity is required")
    @Positive(message = "Capacity must be positive")
    private int capacity;

    /**
     * Current latitude of the vehicle.
     * Valid range: -90 to +90.
     *
     * Wrapper type (Double) allows null validation before processing.
     */
    @NotNull(message = "Current latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
    private Double currentLatitude;

    /**
     * Current longitude of the vehicle.
     * Valid range: -180 to +180.
     *
     * Wrapper type (Double) allows null validation before processing.
     */
    @NotNull(message = "Current longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    private Double currentLongitude;

    /**
     * Human-readable current address of the vehicle.
     * Must not be blank.
     */
    @NotBlank(message = "Current address is required")
    private String currentAddress;
}
