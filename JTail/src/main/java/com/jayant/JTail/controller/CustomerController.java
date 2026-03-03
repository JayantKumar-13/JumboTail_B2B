package com.jayant.JTail.controller;

import com.jayant.JTail.dto.response.ApiResponse;
import com.jayant.JTail.dto.response.CustomerResponse;
import com.jayant.JTail.service.interfaces.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

// Controller for customer-related endpoints: profile retrieval for customers and admins.
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    
    @GetMapping("/me")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<CustomerResponse>> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {

        CustomerResponse response = customerService.getCustomerByEmail(
                userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response, "Profile retrieved"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(customerService.getCustomerById(id),
                        "Customer retrieved"));
    }
}
