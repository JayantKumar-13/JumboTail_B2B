package com.jayant.JTail.dto.response;

import lombok.*;

import java.math.BigDecimal;

// DTO for product details to be returned in API responses, containing all relevant information about the product including pricing, dimensions, stock, and seller details.
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal sellingPrice;
    private Double weightKg;
    private Double lengthCm;
    private Double widthCm;
    private Double heightCm;
    private Integer minOrderQuantity;
    private Integer stockQuantity;
    private String category;
    private String sku;
    private boolean active;
    private Long sellerId;
    private String sellerName;
}
