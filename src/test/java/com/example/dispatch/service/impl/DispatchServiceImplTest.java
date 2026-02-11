package com.example.dispatch.service.impl;

import com.example.dispatch.domain.AssignedOrder;
import com.example.dispatch.domain.DispatchPlan;
import com.example.dispatch.domain.enums.Priority;
import com.example.dispatch.dto.OrderRequest;
import com.example.dispatch.dto.VehicleRequest;
import com.example.dispatch.entity.Order;
import com.example.dispatch.entity.Vehicle;
import com.example.dispatch.exception.ValidationException;
import com.example.dispatch.repository.OrderRepository;
import com.example.dispatch.repository.VehicleRepository;
import com.example.dispatch.strategy.HaversineDistanceStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DispatchServiceImplTest {

    private OrderRepository orderRepository;
    private VehicleRepository vehicleRepository;
    private HaversineDistanceStrategy distanceStrategy;
    private DispatchServiceImpl dispatchService;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        vehicleRepository = mock(VehicleRepository.class);
        distanceStrategy = mock(HaversineDistanceStrategy.class);

        dispatchService = new DispatchServiceImpl(orderRepository, vehicleRepository, distanceStrategy);
    }

    @Test
    void testEmptyOrders_returnsEmptyList() {
        when(orderRepository.findAll()).thenReturn(Collections.emptyList());
        when(vehicleRepository.findAll()).thenReturn(List.of(new Vehicle("V1", 10, 0, 0, "Depot")));

        List<DispatchPlan> plans = dispatchService.generateDispatchPlan();
        assertTrue(plans.isEmpty(), "Expected empty dispatch plan when orders are empty");
    }

    @Test
    void testEmptyVehicles_returnsEmptyList() {
        when(orderRepository.findAll()).thenReturn(List.of(new Order("O1", .0, .0, "Addr", 5, Priority.MEDIUM)));
        when(vehicleRepository.findAll()).thenReturn(Collections.emptyList());

        List<DispatchPlan> plans = dispatchService.generateDispatchPlan();
        assertTrue(plans.isEmpty(), "Expected empty dispatch plan when vehicles are empty");
    }

    @Test
    void testSingleOrderAssignedToSingleVehicle() {
        Order order = new Order("O1", 0.0, 0.0, "Addr", 5, Priority.HIGH);
        Vehicle vehicle = new Vehicle("V1", 10, 0.0, 0.0, "Depot");

        when(orderRepository.findAll()).thenReturn(new ArrayList<>(List.of(order)));
        when(vehicleRepository.findAll()).thenReturn(new ArrayList<>(List.of(vehicle)));
        when(distanceStrategy.calculate(anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(1.0);

        List<DispatchPlan> plans = dispatchService.generateDispatchPlan();

        assertEquals(1, plans.size());
        DispatchPlan plan = plans.get(0);
        assertEquals(vehicle.getVehicleId(), plan.getVehicle().getVehicleId());
        assertEquals(1, plan.getAssignedOrders().size());
        assertEquals(order.getOrderId(), plan.getAssignedOrders().get(0).getOrderId());
        assertEquals(1.0, plan.getTotalDistance());
    }

    @Test
    void testMultipleOrdersMultipleVehicles_greedyAssignment() {
        Order order1 = new Order("O1", 0.0, 0.0, "Addr1", 5, Priority.HIGH);
        Order order2 = new Order("O2", 0.0, 0.0, "Addr2", 5, Priority.MEDIUM);

        Vehicle vehicle1 = new Vehicle("V1", 5, 0.0, 0.0, "Depot1");
        Vehicle vehicle2 = new Vehicle("V2", 10, 0.0, 0.0, "Depot2");

        when(orderRepository.findAll()).thenReturn(new ArrayList<>(List.of(order1, order2)));
        when(vehicleRepository.findAll()).thenReturn(new ArrayList<>(List.of(vehicle1, vehicle2)));
        when(distanceStrategy.calculate(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(1.0); // same distance for simplicity

        List<DispatchPlan> plans = dispatchService.generateDispatchPlan();

        assertEquals(2, plans.size());

        // Total orders assigned
        int totalAssigned = plans.stream()
                .mapToInt(plan -> plan.getAssignedOrders().size())
                .sum();
        assertEquals(2, totalAssigned);

        // Verify capacity respected
        for (DispatchPlan plan : plans) {
            int totalWeight = plan.getAssignedOrders()
                    .stream()
                    .mapToInt(AssignedOrder::getPackageWeight)
                    .sum();
            assertTrue(totalWeight <= plan.getVehicle().getCapacity());
        }
    }

    @Test
    void testOrderCannotBeAssigned_dueToCapacity() {
        Order order1 = new Order("O1", 0.0, 0.0, "Addr1", 10, Priority.HIGH);
        Order order2 = new Order("O2", 0.0, 0.0, "Addr2", 5, Priority.MEDIUM);

        Vehicle vehicle = new Vehicle("V1", 5, 0.0, 0.0, "Depot");

        when(orderRepository.findAll()).thenReturn(new ArrayList<>(List.of(order1, order2)));
        when(vehicleRepository.findAll()).thenReturn(new ArrayList<>(List.of(vehicle)));
        when(distanceStrategy.calculate(anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(1.0);

        List<DispatchPlan> plans = dispatchService.generateDispatchPlan();

        assertEquals(1, plans.size());
        DispatchPlan plan = plans.get(0);

        // Only order2 fits
        assertEquals(1, plan.getAssignedOrders().size());
        assertEquals("O2", plan.getAssignedOrders().get(0).getOrderId());
    }

    // ---------- addOrders() Tests ----------

    @Test
    void testAddOrders_nullOrEmpty_throwsException() {
        ValidationException ex1 = assertThrows(ValidationException.class, () -> dispatchService.addOrders(null));
        assertEquals("Order list cannot be empty", ex1.getMessage());

        ValidationException ex2 = assertThrows(ValidationException.class, () -> dispatchService.addOrders(Collections.emptyList()));
        assertEquals("Order list cannot be empty", ex2.getMessage());
    }

    @Test
    void testAddOrders_duplicateOrderId_throwsException() {
        OrderRequest req = new OrderRequest("O1", .0, .0, "Addr", 5, Priority.HIGH);
        when(orderRepository.existsByOrderId("O1")).thenReturn(true);

        ValidationException ex = assertThrows(ValidationException.class, () -> dispatchService.addOrders(List.of(req)));
        assertEquals("Order with ID O1 already exists", ex.getMessage());
    }

    @Test
    void testAddOrders_validOrders_savesAll() {
        OrderRequest req1 = new OrderRequest("O1", .0, .0, "Addr1", 5, Priority.HIGH);
        OrderRequest req2 = new OrderRequest("O2", .0, .0, "Addr2", 10, Priority.MEDIUM);

        when(orderRepository.existsByOrderId(anyString())).thenReturn(false);

        dispatchService.addOrders(List.of(req1, req2));

        ArgumentCaptor<List<Order>> captor = ArgumentCaptor.forClass(List.class);
        verify(orderRepository).saveAll(captor.capture());
        List<Order> saved = captor.getValue();
        assertEquals(2, saved.size());
        assertEquals("O1", saved.get(0).getOrderId());
        assertEquals("O2", saved.get(1).getOrderId());
    }

    // ---------- addVehicles() Tests ----------

    @Test
    void testAddVehicles_nullOrEmpty_throwsException() {
        ValidationException ex1 = assertThrows(ValidationException.class, () -> dispatchService.addVehicles(null));
        assertEquals("Vehicle list cannot be empty", ex1.getMessage());

        ValidationException ex2 = assertThrows(ValidationException.class, () -> dispatchService.addVehicles(Collections.emptyList()));
        assertEquals("Vehicle list cannot be empty", ex2.getMessage());
    }

    @Test
    void testAddVehicles_duplicateVehicleId_throwsException() {
        VehicleRequest req = new VehicleRequest("V1", 10, .0, .0, "Depot");
        when(vehicleRepository.existsByVehicleId("V1")).thenReturn(true);

        ValidationException ex = assertThrows(ValidationException.class, () -> dispatchService.addVehicles(List.of(req)));
        assertEquals("Vehicle with ID V1 already exists", ex.getMessage());
    }

    @Test
    void testAddVehicles_validVehicles_savesAll() {
        VehicleRequest req1 = new VehicleRequest("V1", 10, .0, .0, "Depot1");
        VehicleRequest req2 = new VehicleRequest("V2", 5, .0, .0, "Depot2");

        when(vehicleRepository.existsByVehicleId(anyString())).thenReturn(false);

        dispatchService.addVehicles(List.of(req1, req2));

        ArgumentCaptor<List<Vehicle>> captor = ArgumentCaptor.forClass(List.class);
        verify(vehicleRepository).saveAll(captor.capture());
        List<Vehicle> saved = captor.getValue();
        assertEquals(2, saved.size());
        assertEquals("V1", saved.get(0).getVehicleId());
        assertEquals("V2", saved.get(1).getVehicleId());
    }
}
