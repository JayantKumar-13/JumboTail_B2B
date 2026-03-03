package com.jayant.JTail.enums;

// Enum representing user roles in the system, used for access control and authorization. Each role corresponds to a set of permissions that determine what actions a user can perform within the application. This enum is used in the User entity to assign roles to users and drive role-based access control throughout the application.
public enum UserRole {
    CUSTOMER,
    SELLER,
    ADMIN
}
