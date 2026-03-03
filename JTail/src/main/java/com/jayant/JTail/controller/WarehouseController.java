package com.jayant.JTail.controller;

import com.jayant.JTail.dto.request.WarehouseRequest;
import com.jayant.JTail.dto.response.ApiResponse;
import com.jayant.JTail.dto.response.WarehouseResponse;
import com.jayant.JTail.service.interfaces.WarehouseService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controller for warehouse-related endpoints: finding nearest warehouse, creating warehouses, and retrieving warehouse details.
@RestController
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    
    @GetMapping("/api/v1/warehouse/nearest")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<ApiResponse<WarehouseResponse>> getNearestWarehouse(
            @RequestParam @NotNull Long sellerId) {

        WarehouseResponse response = warehouseService.findNearestWarehouse(sellerId);
        return ResponseEntity.ok(
                ApiResponse.success(response, "Nearest warehouse found"));
    }

    @PostMapping("/api/v1/warehouses")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<WarehouseResponse>> createWarehouse(
            @Valid @RequestBody WarehouseRequest request) {

        WarehouseResponse response = warehouseService.createWarehouse(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Warehouse created successfully"));
    }

    @GetMapping("/api/v1/warehouses")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<WarehouseResponse>>> getAllWarehouses() {
        return ResponseEntity.ok(
                ApiResponse.success(warehouseService.getAllWarehouses(),
                        "Warehouses retrieved"));
    }

    @GetMapping("/api/v1/warehouses/{id}")
    public ResponseEntity<ApiResponse<WarehouseResponse>> getWarehouse(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(warehouseService.getWarehouseById(id),
                        "Warehouse retrieved"));
    }
}
