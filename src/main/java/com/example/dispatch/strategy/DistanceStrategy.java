package com.example.dispatch.strategy;

/**
 * Strategy interface for calculating the distance between two geographic coordinates.
 * <p>
 * Implementations can use different algorithms or APIs, such as:
 * <ul>
 *     <li>Haversine formula (current implementation)</li>
 *     <li>Google Maps Distance Matrix API</li>
 *     <li>Other routing or GIS-based distance calculations</li>
 * </ul>
 * </p>
 */
public interface DistanceStrategy {

    /**
     * Calculates the distance between two points on Earth given their latitude and longitude.
     *
     * @param lat1 latitude of the first point in decimal degrees
     * @param lon1 longitude of the first point in decimal degrees
     * @param lat2 latitude of the second point in decimal degrees
     * @param lon2 longitude of the second point in decimal degrees
     * @return the distance between the two points in kilometers
     */
    double calculate(double lat1, double lon1, double lat2, double lon2);
}
