package com.example.dispatch.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Represents a vehicle used in the dispatch system.
 * <p>
 * This entity stores information about a vehicle, including its unique identifier, capacity,
 * current location (latitude, longitude), and current address.
 * It is mapped to the "vehicles" table in the database.
 * </p>
 *
 * <p>
 * Validation constraints:
 * <ul>
 *   <li>{@code vehicleId} must be unique and cannot be null.</li>
 *   <li>{@code capacity} must be a positive integer (minimum 1).</li>
 *   <li>{@code currentAddress} cannot be blank.</li>
 * </ul>
 * </p>
 *
 * <p>
 * Lombok annotations are used to automatically generate getters, setters, constructors,
 * equals, hashCode, and toString methods.
 * </p>
 *
 * @author
 */
@Entity
@Table(name = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Vehicle {

    /**
     * Unique identifier of the vehicle.
     * <p>
     * This field is the primary key of the entity, is non-nullable, and cannot be updated
     * after creation.
     * </p>
     */
    @Id
    @EqualsAndHashCode.Include
    @Column(nullable = false, updatable = false)
    private String vehicleId;

    /**
     * Maximum capacity of the vehicle.
     * <p>
     * Must be at least 1.
     * </p>
     */
    @Min(1)
    @Column(nullable = false)
    private int capacity;

    /**
     * Current latitude of the vehicle's location.
     * <p>
     * Represents the geographic latitude coordinate in decimal degrees.
     * </p>
     */
    @Column(nullable = false)
    private double currentLatitude;

    /**
     * Current longitude of the vehicle's location.
     * <p>
     * Represents the geographic longitude coordinate in decimal degrees.
     * </p>
     */
    @Column(nullable = false)
    private double currentLongitude;

    /**
     * Current address of the vehicle.
     * <p>
     * Cannot be blank and represents the textual location of the vehicle.
     * </p>
     */
    @NotBlank
    @Column(nullable = false)
    private String currentAddress;
}
