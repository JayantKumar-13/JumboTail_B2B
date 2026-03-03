package com.jayant.JTail.strategy;

import com.jayant.JTail.dto.response.ShippingChargeResponse;
import com.jayant.JTail.enums.DeliverySpeed;

public interface DeliveryStrategy {

    DeliverySpeed getDeliverySpeed();

    ShippingChargeResponse calculateCharge(double distanceKm, double weightKg);
}