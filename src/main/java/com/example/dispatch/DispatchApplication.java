package com.example.dispatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Dispatch application.
 * <p>
 * This is a Spring Boot application responsible for managing delivery orders,
 * vehicles, and generating optimized dispatch plans.
 * </p>
 *
 * <p>
 * The {@link #main(String[])} method bootstraps the application using
 * {@link SpringApplication#run(Class, String[])}.
 * </p>
 *
 * <p>After startup, the application will be ready to accept REST API requests
 * for adding orders, vehicles, and generating dispatch plans.</p>
 */
@SpringBootApplication
public class DispatchApplication {

    /**
     * Main method to start the Spring Boot application.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(DispatchApplication.class, args);
    }

}
