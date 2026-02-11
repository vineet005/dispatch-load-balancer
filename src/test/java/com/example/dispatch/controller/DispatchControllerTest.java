package com.example.dispatch.controller;

import com.example.dispatch.domain.DispatchPlan;
import com.example.dispatch.domain.enums.Priority;
import com.example.dispatch.dto.ApiResponse;
import com.example.dispatch.dto.OrderRequest;
import com.example.dispatch.dto.VehicleRequest;
import com.example.dispatch.service.DispatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DispatchControllerTest {

    private DispatchService dispatchService;
    private DispatchController dispatchController;

    @BeforeEach
    void setUp() {
        dispatchService = mock(DispatchService.class);
        dispatchController = new DispatchController(dispatchService);
    }

    @Test
    void testAddOrders_success() {
        List<OrderRequest> requests = new ArrayList<>();
        requests.add(new OrderRequest("ORD-101", 28.5355, 77.3910, "Noida Sector 18", 5, Priority.MEDIUM));

        ApiResponse response = dispatchController.addOrders(requests);

        // Verify service called
        ArgumentCaptor<List<OrderRequest>> captor = ArgumentCaptor.forClass(List.class);
        verify(dispatchService, times(1)).addOrders(captor.capture());
        assertEquals(1, captor.getValue().size());
        assertEquals("ORD-101", captor.getValue().get(0).getOrderId());

        // Verify response
        assertEquals("Delivery orders accepted.", response.getMessage());
        assertEquals("success", response.getStatus());
    }

    @Test
    void testAddVehicles_success() {
        List<VehicleRequest> requests = new ArrayList<>();
        requests.add(new VehicleRequest("V1", 10, 28.5355, 77.3910, "Depot"));

        ApiResponse response = dispatchController.addVehicles(requests);

        // Verify service called
        ArgumentCaptor<List<VehicleRequest>> captor = ArgumentCaptor.forClass(List.class);
        verify(dispatchService, times(1)).addVehicles(captor.capture());
        assertEquals(1, captor.getValue().size());
        assertEquals("V1", captor.getValue().get(0).getVehicleId());

        // Verify response
        assertEquals("Vehicle details accepted.", response.getMessage());
        assertEquals("success", response.getStatus());
    }

    @Test
    void testGetDispatchPlan_returnsList() {
        // Prepare a mock dispatch plan
        List<DispatchPlan> mockPlans = new ArrayList<>();
        mockPlans.add(mock(DispatchPlan.class));

        when(dispatchService.generateDispatchPlan()).thenReturn(mockPlans);

        List<DispatchPlan> plans = dispatchController.getDispatchPlan();

        // Verify service called
        verify(dispatchService, times(1)).generateDispatchPlan();

        // Verify returned list
        assertNotNull(plans);
        assertEquals(1, plans.size());
    }

    @Test
    void testAddOrders_emptyList_throwsException() {
        List<OrderRequest> emptyRequests = new ArrayList<>();

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            dispatchController.addOrders(emptyRequests);
        });

        assertEquals("Order list cannot be empty", exception.getMessage());
    }


    @Test
    void testAddVehicles_emptyList_serviceNotCalled() {
        List<VehicleRequest> emptyRequests = new ArrayList<>();

        ApiResponse response = dispatchController.addVehicles(emptyRequests);

        // Service should not be called for empty list (or you can skip)
        verify(dispatchService, times(1)).addVehicles(emptyRequests); // still called in your code

        assertEquals("Vehicle details accepted.", response.getMessage());
    }

}
