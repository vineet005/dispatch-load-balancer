package com.example.dispatch.strategy;

import org.springframework.stereotype.Component;

/**
 * Implementation of {@link DistanceStrategy} using the Haversine formula
 * to calculate the great-circle distance between two points on Earth.
 * <p>
 * The Haversine formula accounts for the spherical shape of the Earth and
 * calculates the shortest distance over the Earth's surface.
 * </p>
 *
 * <p>Formula:</p>
 * <pre>
 * dLat = lat2 - lat1
 * dLon = lon2 - lon1
 * a = sin²(dLat/2) + cos(lat1) * cos(lat2) * sin²(dLon/2)
 * c = 2 * atan2(√a, √(1−a))
 * distance = EARTH_RADIUS * c
 * </pre>
 *
 * <p>Distance is returned in kilometers.</p>
 */
@Component
public class HaversineDistanceStrategy implements DistanceStrategy {

    /** Average radius of the Earth in kilometers. */
    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Calculates the great-circle distance between two points on Earth using
     * latitude and longitude coordinates.
     *
     * @param lat1 latitude of the first point in decimal degrees
     * @param lon1 longitude of the first point in decimal degrees
     * @param lat2 latitude of the second point in decimal degrees
     * @param lon2 longitude of the second point in decimal degrees
     * @return the distance between the two points in kilometers
     */
    @Override
    public double calculate(double lat1, double lon1, double lat2, double lon2) {

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }
}
