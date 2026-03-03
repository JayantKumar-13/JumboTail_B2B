package com.jayant.JTail.dto.response;

import com.jayant.JTail.enums.DeliverySpeed;
import com.jayant.JTail.enums.TransportMode;
import lombok.*;

import java.math.BigDecimal;
// DTO for the shipping charge calculation response, containing the calculated shipping charge along with details about the transport mode, delivery speed, distance, and charge breakdown.
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ShippingChargeResponse {

    private BigDecimal shippingCharge;
    private Double distanceKm;

    private TransportMode transportMode;

    private DeliverySpeed deliverySpeed;

    private BigDecimal baseCourierCharge;
    private BigDecimal expressSurcharge;

    private BigDecimal distanceBasedCharge;
}
