package com.jayant.JTail.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

// DTO for product creation and update requests, with validation annotations to ensure required fields are provided and properly formatted.
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 200)
    private String name;

    @Size(max = 500)
    private String description;

    @NotNull(message = "Selling price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal sellingPrice;


    @NotNull(message = "Weight is required")
    @DecimalMin(value = "0.001", message = "Weight must be positive")
    private Double weightKg;

    @NotNull(message = "Length is required")
    @DecimalMin(value = "0.1")
    private Double lengthCm;

    @NotNull(message = "Width is required")
    @DecimalMin(value = "0.1")
    private Double widthCm;

    @NotNull(message = "Height is required")
    @DecimalMin(value = "0.1")
    private Double heightCm;

    @Min(value = 1, message = "Minimum order quantity must be at least 1")
    private Integer minOrderQuantity = 1;

    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stockQuantity = 0;

    @Size(max = 100)
    private String category;

    @Size(max = 50)
    private String sku;
}
