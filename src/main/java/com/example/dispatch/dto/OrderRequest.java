package com.example.dispatch.dto;

import com.example.dispatch.domain.enums.Priority;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO representing a delivery order submitted via API.
 *
 * <p>
 * This class defines the external API contract for order creation.
 * It is validated at the controller boundary using Jakarta Bean Validation.
 *
 * <p>
 * Validation Strategy:
 * <ul>
 *     <li>Ensures required fields are present</li>
 *     <li>Validates geographic coordinate ranges</li>
 *     <li>Ensures package weight is positive</li>
 *     <li>Ensures priority is explicitly provided</li>
 * </ul>
 *
 * <p>
 * Important:
 * This is NOT a JPA entity.
 * It exists purely for input validation and API-layer decoupling.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {

    /**
     * Unique identifier for the order.
     * Must not be null or blank.
     */
    @NotBlank(message = "Order ID is required")
    private String orderId;

    /**
     * Latitude coordinate of delivery location.
     * Valid range: -90 to +90.
     * Wrapper type (Double) is used instead of primitive
     * to allow proper null validation.
     */
    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
    private Double latitude;

    /**
     * Longitude coordinate of delivery location.
     * Valid range: -180 to +180.
     * Wrapper type (Double) is used instead of primitive
     * to allow proper null validation.
     */
    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    private Double longitude;

    /**
     * Human-readable delivery address.
     * Must not be blank.
     */
    @NotBlank(message = "Address is required")
    private String address;

    /**
     * Weight of the package in kilograms.
     * Must be a positive value.
     * Primitive int is acceptable here because @NotNull
     * is enforced before deserialization.
     */
    @NotNull(message = "Package weight is required")
    @Positive(message = "Package weight must be positive")
    private int packageWeight;

    /**
     * Priority level of the order.
     * Must be explicitly provided.
     *
     * Acceptable values:
     * HIGH, MEDIUM, LOW
     */
    @NotNull(message = "Priority is required")
    private Priority priority;
}
