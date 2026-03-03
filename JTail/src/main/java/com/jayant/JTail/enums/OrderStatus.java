package com.jayant.JTail.enums;
// Enum representing the various statuses an order can be in throughout its lifecycle, from placement to delivery or cancellation. This enum is used in the Order entity to track the current state of each order and drive business logic based on status transitions.
public enum OrderStatus {
    PENDING,       
    CONFIRMED,      
    DROPPED_AT_WH,  
    IN_TRANSIT,    
    DELIVERED,      
    CANCELLED      
}
