package com.example.dispatch.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HaversineDistanceStrategyTest {

    private HaversineDistanceStrategy distanceStrategy;

    @BeforeEach
    void setUp() {
        distanceStrategy = new HaversineDistanceStrategy();
    }

    @Test
    void testDistanceBetweenDifferentPoints() {
        // Example: London (51.5074° N, 0.1278° W) to Paris (48.8566° N, 2.3522° E)
        double distance = distanceStrategy.calculate(51.5074, -0.1278, 48.8566, 2.3522);

        // Approximate distance ~343 km
        assertEquals(343, distance, 1.0, "Distance between London and Paris should be ~343 km");
    }

    @Test
    void testDistanceSamePointIsZero() {
        double distance = distanceStrategy.calculate(51.5074, -0.1278, 51.5074, -0.1278);
        assertEquals(0.0, distance, 0.0001, "Distance between same coordinates should be 0");
    }

    @Test
    void testDistanceAntipodalPoints() {
        // Approximate antipodal points: Wellington, NZ (41.2865 S, 174.7762 E)
        // and near Salamanca, Spain (41.2865 N, 5.2238 W)
        double distance = distanceStrategy.calculate(-41.2865, 174.7762, 41.2865, -5.2238);

        // Distance should be close to Earth's circumference along great circle (~20015 km)
        assertEquals(20015, distance, 10, "Distance between antipodal points should be ~20015 km");
    }

    @Test
    void testDistanceEquatorCrossing() {
        // Point just north of equator to point just south
        double distance = distanceStrategy.calculate(0.0001, 30.0, -0.0001, 30.0);

        // Very small distance (~0.022 km)
        assertTrue(distance > 0, "Distance should be positive");
        assertTrue(distance < 0.05, "Distance should be very small, <0.05 km");
    }

    @Test
    void testDistancePrimeMeridianCrossing() {
        // Crossing 0° longitude
        double distance = distanceStrategy.calculate(51.0, -0.0001, 51.0, 0.0001);

        assertTrue(distance > 0, "Distance should be positive");
        assertTrue(distance < 0.05, "Distance should be very small, <0.05 km");
    }
}
