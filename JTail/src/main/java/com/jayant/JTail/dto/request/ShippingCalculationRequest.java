package com.jayant.JTail.dto.request;

import com.jayant.JTail.enums.DeliverySpeed;
import jakarta.validation.constraints.NotNull;
import lombok.*;

// DTO for shipping calculation requests, with validation annotations to ensure required fields are provided and properly formatted.
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ShippingCalculationRequest {

    @NotNull(message = "Seller ID is required")
    private Long sellerId;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Delivery speed is required")
    private DeliverySpeed deliverySpeed;
}
