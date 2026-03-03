package com.jayant.JTail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Entry point for the Jumbotail B2B E-Commerce Shipping Estimator application.
 *
 * This application allows:
 *  - Customer (Kirana store) signup and JWT-based login
 *  - Seller and Warehouse registration
 *  - Finding the nearest warehouse for a seller
 *  - Calculating shipping charges using real road distances (via OSRM API)
 *  - Strategy-based delivery speed pricing
 */
@SpringBootApplication
@EnableCaching   // Activates Spring's caching abstraction (Caffeine backend)
public class JumbotailApplication {

    public static void main(String[] args) {
        SpringApplication.run(JumbotailApplication.class, args);
    }
}
