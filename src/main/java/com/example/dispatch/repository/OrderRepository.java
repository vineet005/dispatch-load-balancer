package com.example.dispatch.repository;

import com.example.dispatch.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Order} entities in the dispatch system.
 * <p>
 * Provides standard CRUD operations inherited from {@link JpaRepository}.
 * The {@code save()} and {@code saveAll()} methods act as UPSERT operations
 * because {@code orderId} is the primary key.
 * </p>
 *
 * <p>Additional query methods can be defined here if needed.</p>
 *
 * Example usage:
 * <pre>
 * {@code
 * @Autowired
 * private OrderRepository orderRepository;
 *
 * boolean exists = orderRepository.existsByOrderId("ORDER123");
 * }
 * </pre>
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    /**
     * Checks if an {@link Order} with the given {@code orderId} exists.
     *
     * @param orderId the unique identifier of the order
     * @return {@code true} if an order with the given ID exists, {@code false} otherwise
     */
    boolean existsByOrderId(String orderId);
}
