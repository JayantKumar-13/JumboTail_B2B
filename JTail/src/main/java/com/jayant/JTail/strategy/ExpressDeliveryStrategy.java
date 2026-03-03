package com.jayant.JTail.strategy;

import com.jayant.JTail.dto.response.ShippingChargeResponse;
import com.jayant.JTail.enums.DeliverySpeed;
import com.jayant.JTail.enums.TransportMode;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class ExpressDeliveryStrategy implements DeliveryStrategy {

    private static final BigDecimal BASE_COURIER_CHARGE = new BigDecimal("10.00");
    private static final BigDecimal EXPRESS_RATE_PER_KG = new BigDecimal("1.20");

    @Override
    public DeliverySpeed getDeliverySpeed() {
        return DeliverySpeed.EXPRESS;
    }

    @Override
    public ShippingChargeResponse calculateCharge(double distanceKm, double weightKg) {
        TransportMode transportMode = TransportMode.fromDistance(distanceKm);

        BigDecimal distanceBasedCharge = BigDecimal.valueOf(
                transportMode.getRatePerKmPerKg() * distanceKm * weightKg
        ).setScale(2, RoundingMode.HALF_UP);

        BigDecimal expressSurcharge = EXPRESS_RATE_PER_KG
                .multiply(BigDecimal.valueOf(weightKg))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal total = BASE_COURIER_CHARGE
                .add(expressSurcharge)
                .add(distanceBasedCharge);

        return ShippingChargeResponse.builder()
                .shippingCharge(total)
                .distanceKm(distanceKm)
                .transportMode(transportMode)
                .deliverySpeed(DeliverySpeed.EXPRESS)
                .baseCourierCharge(BASE_COURIER_CHARGE)
                .expressSurcharge(expressSurcharge)
                .distanceBasedCharge(distanceBasedCharge)
                .build();
    }
}