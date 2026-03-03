package com.jayant.JTail.dto.response;

import lombok.*;

import java.math.BigDecimal;

// DTO for the combined shipping charge calculation response, containing the total shipping charge, nearest warehouse details, and a breakdown of the charge by transport mode and delivery speed.
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CombinedShippingResponse {

    private BigDecimal shippingCharge;
    private WarehouseResponse nearestWarehouse;
    private ShippingChargeResponse chargeBreakdown;
}
