package com.jayant.JTail.controller;

import com.jayant.JTail.dto.request.ShippingCalculationRequest;
import com.jayant.JTail.dto.response.ApiResponse;
import com.jayant.JTail.dto.response.CombinedShippingResponse;
import com.jayant.JTail.dto.response.ShippingChargeResponse;
import com.jayant.JTail.enums.DeliverySpeed;
import com.jayant.JTail.service.interfaces.ShippingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Controller for shipping-related endpoints: calculating shipping charges and combined shipping details.
@RestController
@RequestMapping("/api/v1/shipping-charge")
@RequiredArgsConstructor
public class ShippingController {

    private final ShippingService shippingService;

   
    @GetMapping
    public ResponseEntity<ApiResponse<ShippingChargeResponse>> getShippingCharge(
            @RequestParam Long warehouseId,
            @RequestParam Long customerId,
            @RequestParam Long productId,
            @RequestParam DeliverySpeed deliverySpeed) {

        ShippingChargeResponse response = shippingService.getShippingCharge(
                warehouseId, customerId, productId, deliverySpeed
        );
        return ResponseEntity.ok(
                ApiResponse.success(response, "Shipping charge calculated successfully"));
    }

    
    @PostMapping("/calculate")
    public ResponseEntity<ApiResponse<CombinedShippingResponse>> calculateShipping(
            @Valid @RequestBody ShippingCalculationRequest request) {

        CombinedShippingResponse response = shippingService.calculateShipping(request);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Shipping calculated successfully"));
    }
}
