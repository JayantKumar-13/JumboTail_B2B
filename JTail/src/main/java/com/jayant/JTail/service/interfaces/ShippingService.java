package com.jayant.JTail.service.interfaces;

import com.jayant.JTail.dto.request.ShippingCalculationRequest;
import com.jayant.JTail.dto.response.CombinedShippingResponse;
import com.jayant.JTail.dto.response.ShippingChargeResponse;
import com.jayant.JTail.enums.DeliverySpeed;

public interface ShippingService {

    ShippingChargeResponse getShippingCharge(Long warehouseId,
                                             Long customerId,
                                             Long productId,
                                             DeliverySpeed deliverySpeed);

    CombinedShippingResponse calculateShipping(ShippingCalculationRequest request);
}