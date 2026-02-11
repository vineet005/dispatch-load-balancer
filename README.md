# Dispatch Load Balancer

[![Java](https://img.shields.io/badge/Java-17-blue)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-green)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)

---

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [API Endpoints](#api-endpoints)
- [Setup & Installation](#setup--installation)
- [Running Tests](#running-tests)
- [Technology Stack](#technology-stack)
- [License](#license)

---

## Overview

The **Dispatch Load Balancer** is a Spring Boot application that optimizes the assignment of delivery orders to vehicles.  
It considers **order priority, vehicle capacity, and distance** to generate efficient dispatch plans for logistics operations.  

Key functionality:
- Accept orders and vehicles via REST API
- Assign orders to nearest vehicles based on capacity
- Prioritize orders (High → Medium → Low)
- Calculate distances using Haversine formula
- Provide optimized dispatch plans per vehicle

---

## Features

- RESTful API to add orders and vehicles
- Automated validation using **Jakarta Bean Validation**
- Greedy assignment algorithm for dispatch optimization
- Graceful handling of unassignable orders
- Extensible distance strategy (Haversine, future Google Maps, etc.)
- Comprehensive exception handling and meaningful error messages

---

## Architecture

[Client] --> [DispatchController] --> [DispatchServiceImpl] --> [Repositories]
|
--> [DistanceStrategy] --> HaversineDistanceStrategy


- **Controller:** Thin layer, delegates logic to service  
- **Service:** Core dispatch algorithm and validation  
- **Repository:** Persistence via Spring Data JPA  
- **Strategy:** Pluggable distance calculation  

---

## API Endpoints

| Method | Endpoint                | Description                         | Request Body                     | Response        |
|--------|------------------------|-------------------------------------|---------------------------------|----------------|
| POST   | /api/dispatch/orders   | Add delivery orders                 | List<OrderRequest>               | ApiResponse     |
| POST   | /api/dispatch/vehicles | Add vehicle details                 | List<VehicleRequest>             | ApiResponse     |
| GET    | /api/dispatch/plan     | Retrieve optimized dispatch plan    | None                             | List<DispatchPlan> |

### Example Order Request
```json
{
  "orderId": "ORD-101",
  "latitude": 28.5355,
  "longitude": 77.3910,
  "address": "Noida Sector 18",
  "packageWeight": 5.0,
  "priority": 2
}

---

Technology Stack

Language: Java 17

Framework: Spring Boot 3.x

Persistence: Spring Data JPA / Hibernate

Validation: Jakarta Bean Validation

Testing: JUnit 5, Mockito

Distance Calculation: Haversine formula

Build Tool: Maven

---
