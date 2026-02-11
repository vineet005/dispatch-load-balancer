package com.example.dispatch.domain.enums;

/**
 * Represents the priority level of a delivery order.
 *
 * <p>
 * Priority determines the dispatch sequencing logic:
 * <ul>
 *     <li>{@link #HIGH}   - Orders that must be delivered urgently.</li>
 *     <li>{@link #MEDIUM} - Standard delivery orders.</li>
 *     <li>{@link #LOW}    - Non-urgent deliveries.</li>
 * </ul>
 *
 * <p>
 * Dispatch Strategy Rule:
 * Higher priority orders are always processed before lower priority ones.
 *
 * <p>
 * NOTE:
 * This enum is used in:
 * <ul>
 *     <li>OrderRequest DTO for incoming API requests</li>
 *     <li>Dispatch sorting logic in service layer</li>
 * </ul>
 *
 * <p>
 * Recommended API Usage:
 * Priority should be sent as STRING in API requests:
 * <pre>
 *     "priority": "HIGH"
 * </pre>
 *
 * Sending numeric values is not recommended and may cause deserialization errors.
 */
public enum Priority {

    /**
     * Urgent delivery.
     * Highest dispatch precedence.
     */
    HIGH,

    /**
     * Standard delivery.
     * Processed after HIGH priority orders.
     */
    MEDIUM,

    /**
     * Non-urgent delivery.
     * Lowest dispatch precedence.
     */
    LOW
}
