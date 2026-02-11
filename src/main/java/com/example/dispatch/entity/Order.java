package com.example.dispatch.entity;

import com.example.dispatch.domain.enums.Priority;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * JPA Entity representing a delivery order stored in the database.
 *
 * <p>
 * This entity maps to the "orders" table in PostgreSQL and
 * represents the persistent state of an order.
 *
 * <p>
 * Design Decisions:
 * <ul>
 *     <li>Order ID is the primary key (business key)</li>
 *     <li>Enum stored as STRING for database readability and safety</li>
 *     <li>Validation annotations ensure data integrity at persistence layer</li>
 * </ul>
 *
 * <p>
 * Important:
 * This class represents the persistence model.
 * It should not be directly exposed via API.
 * API interactions should use DTO classes.
 */
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Order {

    /**
     * Unique identifier of the order.
     *
     * <p>
     * Acts as the primary key.
     * Not updatable once created.
     */
    @Id
    @EqualsAndHashCode.Include
    @Column(nullable = false, updatable = false)
    private String orderId;

    /**
     * Latitude coordinate of delivery location.
     *
     * <p>
     * Must not be null.
     * Database column is non-nullable.
     */
    @NotNull
    @Column(nullable = false)
    private Double latitude;

    /**
     * Longitude coordinate of delivery location.
     *
     * <p>
     * Must not be null.
     * Database column is non-nullable.
     */
    @NotNull
    @Column(nullable = false)
    private Double longitude;

    /**
     * Human-readable delivery address.
     *
     * <p>
     * Must not be blank.
     * Stored as non-nullable column.
     */
    @NotBlank
    @Column(nullable = false)
    private String address;

    /**
     * Weight of the package in kilograms.
     *
     * <p>
     * Must be at least 1 kg.
     * Database column is non-nullable.
     */
    @Min(1)
    @Column(nullable = false)
    private int packageWeight;

    /**
     * Priority level of the order.
     *
     * <p>
     * Stored as STRING in database to:
     * <ul>
     *     <li>Improve readability</li>
     *     <li>Avoid ordinal index issues</li>
     *     <li>Prevent data corruption if enum order changes</li>
     * </ul>
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;
}
