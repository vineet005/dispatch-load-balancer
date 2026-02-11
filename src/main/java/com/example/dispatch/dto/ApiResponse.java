package com.example.dispatch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Standardized API response wrapper.
 *
 * <p>
 * This DTO provides a consistent structure for all non-data responses
 * returned by the application (e.g., success messages, error messages).
 *
 * <p>
 * Using a unified response model ensures:
 * <ul>
 *     <li>Consistent API contract</li>
 *     <li>Predictable frontend integration</li>
 *     <li>Centralized error handling compatibility</li>
 * </ul>
 *
 * <p>
 * Example Success Response:
 * <pre>
 * {
 *   "message": "Delivery orders accepted.",
 *   "status": "success"
 * }
 * </pre>
 *
 * Example Error Response:
 * <pre>
 * {
 *   "message": "Order ID must not be empty",
 *   "status": "error"
 * }
 * </pre>
 */
@Data
@AllArgsConstructor
public class ApiResponse {

    /**
     * Human-readable message describing the result
     * of the API operation.
     */
    private String message;

    /**
     * Operation status.
     * Typically:
     * <ul>
     *     <li>"success"</li>
     *     <li>"error"</li>
     * </ul>
     */
    private String status;
}
