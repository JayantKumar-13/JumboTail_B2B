package com.jayant.JTail.controller;

import com.jayant.JTail.dto.request.CustomerSignupRequest;
import com.jayant.JTail.dto.request.LoginRequest;
import com.jayant.JTail.dto.request.SellerSignupRequest;
import com.jayant.JTail.dto.response.ApiResponse;
import com.jayant.JTail.dto.response.AuthResponse;
import com.jayant.JTail.service.interfaces.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Controller for authentication-related endpoints: customer/seller signup and login.
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup/customer")
    public ResponseEntity<ApiResponse<AuthResponse>> signupCustomer(
            @Valid @RequestBody CustomerSignupRequest request) {

        AuthResponse response = authService.signupCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, response.getMessage()));
    }

    @PostMapping("/signup/seller")
    public ResponseEntity<ApiResponse<AuthResponse>> signupSeller(
            @Valid @RequestBody SellerSignupRequest request) {

        AuthResponse response = authService.signupSeller(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, response.getMessage()));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, response.getMessage()));
    }
}
