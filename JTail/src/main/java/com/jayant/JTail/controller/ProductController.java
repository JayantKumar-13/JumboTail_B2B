package com.jayant.JTail.controller;

import com.jayant.JTail.dto.request.ProductRequest;
import com.jayant.JTail.dto.response.ApiResponse;
import com.jayant.JTail.dto.response.ProductResponse;
import com.jayant.JTail.repository.SellerRepository;
import com.jayant.JTail.service.interfaces.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controller for product-related endpoints: listing, adding, updating, and deactivating products.
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService    productService;
    private final SellerRepository  sellerRepository;

    
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {
        return ResponseEntity.ok(
                ApiResponse.success(productService.getAllActiveProducts(), "Products retrieved"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(productService.getProductById(id), "Product retrieved"));
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProductsBySeller(
            @PathVariable Long sellerId) {
        return ResponseEntity.ok(
                ApiResponse.success(productService.getProductsBySeller(sellerId),
                        "Seller products retrieved"));
    }

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<ProductResponse>> addProduct(
            @Valid @RequestBody ProductRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long sellerId = getSellerId(userDetails.getUsername());
        ProductResponse response = productService.addProduct(sellerId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Product added successfully"));
    }

    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long sellerId = getSellerId(userDetails.getUsername());
        ProductResponse response = productService.updateProduct(id, sellerId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Product updated"));
    }

    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<ApiResponse<Void>> deactivateProduct(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long sellerId = getSellerId(userDetails.getUsername());
        productService.deactivateProduct(id, sellerId);
        return ResponseEntity.ok(ApiResponse.success(null, "Product deactivated"));
    }

    private Long getSellerId(String email) {
        return sellerRepository.findByUserEmail(email)
                .orElseThrow(() -> new com.jayant.JTail.exception.ResourceNotFoundException(
                        "Seller profile not found for email: " + email))
                .getId();
    }
}
