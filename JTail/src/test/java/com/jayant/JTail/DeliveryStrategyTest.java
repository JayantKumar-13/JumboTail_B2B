package com.jayant.JTail;

import com.jayant.JTail.dto.response.ShippingChargeResponse;
import com.jayant.JTail.enums.DeliverySpeed;
import com.jayant.JTail.enums.TransportMode;
import com.jayant.JTail.strategy.ExpressDeliveryStrategy;
import com.jayant.JTail.strategy.StandardDeliveryStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryStrategyTest {

    private StandardDeliveryStrategy standardStrategy;
    private ExpressDeliveryStrategy expressStrategy;

    @BeforeEach
    void setUp() {
        standardStrategy = new StandardDeliveryStrategy();
        expressStrategy = new ExpressDeliveryStrategy();
    }

    @Test
    @DisplayName("Standard: returns STANDARD delivery speed")
    void testStandard_DeliverySpeedType() {
        assertEquals(DeliverySpeed.STANDARD, standardStrategy.getDeliverySpeed());
    }

    @ParameterizedTest(name = "distance={0}km, weight={1}kg → expected={2}")
    @CsvSource({
        "50.0,  1.0, 160.00",
        "80.0,  2.0, 490.00",
        "99.0,  0.5, 158.50",
        "100.0, 1.0, 210.00",
        "300.0, 2.0, 1210.00",
        "499.0, 0.5, 509.00",
        "500.0, 1.0, 510.00",
        "1000.0,10.0,10010.00",
    })
    @DisplayName("Standard: charge = Rs 10 + (rate × dist × weight)")
    void testStandard_ChargeFormula(double distance, double weight, String expectedStr) {
        ShippingChargeResponse response = standardStrategy.calculateCharge(distance, weight);

        assertEquals(new BigDecimal(expectedStr), response.getShippingCharge());
        assertNull(response.getExpressSurcharge(), "Standard should have no express surcharge");
    }

    @Test
    @DisplayName("Standard: Mini Van selected for 0–99 km")
    void testStandard_TransportMode_MiniVan() {
        ShippingChargeResponse r = standardStrategy.calculateCharge(99.0, 1.0);
        assertEquals(TransportMode.MINI_VAN, r.getTransportMode());
    }

    @Test
    @DisplayName("Standard: Truck selected for 100–499 km")
    void testStandard_TransportMode_Truck() {
        ShippingChargeResponse r = standardStrategy.calculateCharge(200.0, 1.0);
        assertEquals(TransportMode.TRUCK, r.getTransportMode());
    }

    @Test
    @DisplayName("Standard: Aeroplane selected for 500+ km")
    void testStandard_TransportMode_Aeroplane() {
        ShippingChargeResponse r = standardStrategy.calculateCharge(600.0, 1.0);
        assertEquals(TransportMode.AEROPLANE, r.getTransportMode());
    }

    @Test
    @DisplayName("Express: returns EXPRESS delivery speed")
    void testExpress_DeliverySpeedType() {
        assertEquals(DeliverySpeed.EXPRESS, expressStrategy.getDeliverySpeed());
    }

    @Test
    @DisplayName("Express: surcharge = Rs 1.2/kg")
    void testExpress_SurchargeCalculation() {
        ShippingChargeResponse response = expressStrategy.calculateCharge(50.0, 2.0);
        assertEquals(new BigDecimal("2.40"), response.getExpressSurcharge());
    }

    @Test
    @DisplayName("Express: total = base + surcharge + distance charge")
    void testExpress_TotalCharge() {
        ShippingChargeResponse response = expressStrategy.calculateCharge(50.0, 1.0);
        assertEquals(new BigDecimal("161.20"), response.getShippingCharge());
    }

    @Test
    @DisplayName("Express charge is always higher than standard for same params")
    void testExpress_AlwaysHigherThanStandard() {
        double distance = 300.0;
        double weight = 5.0;

        BigDecimal standardCharge = standardStrategy.calculateCharge(distance, weight).getShippingCharge();
        BigDecimal expressCharge = expressStrategy.calculateCharge(distance, weight).getShippingCharge();

        assertTrue(expressCharge.compareTo(standardCharge) > 0,
                "Express should always be more expensive than Standard");
    }

    @Test
    @DisplayName("TransportMode boundary: exactly 100km → Truck")
    void testTransportMode_BoundaryAt100km() {
        assertEquals(TransportMode.TRUCK, TransportMode.fromDistance(100.0));
    }

    @Test
    @DisplayName("TransportMode boundary: exactly 500km → Aeroplane")
    void testTransportMode_BoundaryAt500km() {
        assertEquals(TransportMode.AEROPLANE, TransportMode.fromDistance(500.0));
    }

    @Test
    @DisplayName("TransportMode: 0km → Mini Van")
    void testTransportMode_ZeroDistance() {
        assertEquals(TransportMode.MINI_VAN, TransportMode.fromDistance(0.0));
    }
}