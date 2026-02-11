package com.example.dispatch.repository;

import com.example.dispatch.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Vehicle} entities in the dispatch system.
 * <p>
 * Provides standard CRUD operations inherited from {@link JpaRepository}.
 * The {@code save()} and {@code saveAll()} methods act as UPSERT operations
 * because {@code vehicleId} is the primary key.
 * </p>
 *
 * <p>Additional query methods can be defined here if needed.</p>
 *
 * Example usage:
 * <pre>
 * {@code
 * @Autowired
 * private VehicleRepository vehicleRepository;
 *
 * boolean exists = vehicleRepository.existsByVehicleId("VEH123");
 * }
 * </pre>
 */
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String> {

    /**
     * Checks if a {@link Vehicle} with the given {@code vehicleId} exists.
     *
     * @param vehicleId the unique identifier of the vehicle
     * @return {@code true} if a vehicle with the given ID exists, {@code false} otherwise
     */
    boolean existsByVehicleId(String vehicleId);
}
