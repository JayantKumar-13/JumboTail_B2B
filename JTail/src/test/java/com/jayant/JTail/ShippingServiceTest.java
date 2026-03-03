package com.jayant.JTail;

import com.jayant.JTail.dto.request.ShippingCalculationRequest;
import com.jayant.JTail.dto.response.CombinedShippingResponse;
import com.jayant.JTail.dto.response.ShippingChargeResponse;
import com.jayant.JTail.dto.response.WarehouseResponse;
import com.jayant.JTail.entity.Customer;
import com.jayant.JTail.entity.Product;
import com.jayant.JTail.entity.Warehouse;
import com.jayant.JTail.enums.DeliverySpeed;
import com.jayant.JTail.enums.TransportMode;
import com.jayant.JTail.exception.ResourceNotFoundException;
import com.jayant.JTail.repository.CustomerRepository;
import com.jayant.JTail.repository.ProductRepository;
import com.jayant.JTail.repository.WarehouseRepository;
import com.jayant.JTail.service.impl.ShippingServiceImpl;
import com.jayant.JTail.service.interfaces.OSRMService;
import com.jayant.JTail.service.interfaces.WarehouseService;
import com.jayant.JTail.strategy.DeliveryStrategyFactory;
import com.jayant.JTail.strategy.ExpressDeliveryStrategy;
import com.jayant.JTail.strategy.StandardDeliveryStrategy;
import com.jayant.JTail.utils.GeometryUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShippingServiceTest {

    @Mock private CustomerRepository customerRepository;
    @Mock private WarehouseRepository warehouseRepository;
    @Mock private ProductRepository productRepository;
    @Mock private OSRMService osrmService;
    @Mock private WarehouseService warehouseService;
    @Mock private DeliveryStrategyFactory strategyFactory;

    @InjectMocks
    private ShippingServiceImpl shippingService;

    private Warehouse testWarehouse;
    private Customer testCustomer;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testWarehouse = new Warehouse();
        testWarehouse.setId(1L);
        testWarehouse.setName("BLR_Warehouse");
        testWarehouse.setLocation(GeometryUtil.createPoint(77.5946, 12.9716));

        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setStoreName("Test Kirana");
        testCustomer.setLocation(GeometryUtil.createPoint(72.8777, 19.0760));

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Maggie 500g");
        testProduct.setWeightKg(0.5);
    }

    @Test
    @DisplayName("Standard delivery - Mini Van route (< 100 km)")
    void testStandardDelivery_MiniVan() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(testWarehouse));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(osrmService.getDistanceKm(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(50.0);

        StandardDeliveryStrategy strategy = new StandardDeliveryStrategy();
        when(strategyFactory.getStrategy(DeliverySpeed.STANDARD)).thenReturn(strategy);

        ShippingChargeResponse response = shippingService.getShippingCharge(
                1L, 1L, 1L, DeliverySpeed.STANDARD
        );

        assertNotNull(response);
        assertEquals(TransportMode.MINI_VAN, response.getTransportMode());
        assertEquals(new BigDecimal("85.00"), response.getShippingCharge());
        assertEquals(DeliverySpeed.STANDARD, response.getDeliverySpeed());
        assertNull(response.getExpressSurcharge());
        assertEquals(50.0, response.getDistanceKm());
    }

    @Test
    @DisplayName("Standard delivery - Truck route (100–500 km)")
    void testStandardDelivery_Truck() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(testWarehouse));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(osrmService.getDistanceKm(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(300.0);

        StandardDeliveryStrategy strategy = new StandardDeliveryStrategy();
        when(strategyFactory.getStrategy(DeliverySpeed.STANDARD)).thenReturn(strategy);

        ShippingChargeResponse response = shippingService.getShippingCharge(
                1L, 1L, 1L, DeliverySpeed.STANDARD
        );

        assertEquals(TransportMode.TRUCK, response.getTransportMode());
        assertEquals(new BigDecimal("310.00"), response.getShippingCharge());
    }

    @Test
    @DisplayName("Standard delivery - Aeroplane route (500+ km)")
    void testStandardDelivery_Aeroplane() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(testWarehouse));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(osrmService.getDistanceKm(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(1000.0);

        StandardDeliveryStrategy strategy = new StandardDeliveryStrategy();
        when(strategyFactory.getStrategy(DeliverySpeed.STANDARD)).thenReturn(strategy);

        ShippingChargeResponse response = shippingService.getShippingCharge(
                1L, 1L, 1L, DeliverySpeed.STANDARD
        );

        assertEquals(TransportMode.AEROPLANE, response.getTransportMode());
        assertEquals(new BigDecimal("510.00"), response.getShippingCharge());
    }

    @Test
    @DisplayName("Express delivery - includes Rs 1.2/kg surcharge")
    void testExpressDelivery_WithSurcharge() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(testWarehouse));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(osrmService.getDistanceKm(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(50.0);

        ExpressDeliveryStrategy strategy = new ExpressDeliveryStrategy();
        when(strategyFactory.getStrategy(DeliverySpeed.EXPRESS)).thenReturn(strategy);

        ShippingChargeResponse response = shippingService.getShippingCharge(
                1L, 1L, 1L, DeliverySpeed.EXPRESS
        );

        assertEquals(TransportMode.MINI_VAN, response.getTransportMode());
        assertEquals(new BigDecimal("0.60"), response.getExpressSurcharge());
        assertEquals(new BigDecimal("85.60"), response.getShippingCharge());
        assertEquals(DeliverySpeed.EXPRESS, response.getDeliverySpeed());
    }

    @Test
    @DisplayName("Throws ResourceNotFoundException for invalid warehouseId")
    void testGetShippingCharge_InvalidWarehouse() {
        when(warehouseRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                shippingService.getShippingCharge(999L, 1L, 1L, DeliverySpeed.STANDARD)
        );
    }

    @Test
    @DisplayName("Throws ResourceNotFoundException for invalid customerId")
    void testGetShippingCharge_InvalidCustomer() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(testWarehouse));
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                shippingService.getShippingCharge(1L, 999L, 1L, DeliverySpeed.STANDARD)
        );
    }

    @Test
    @DisplayName("Throws ResourceNotFoundException for invalid productId")
    void testGetShippingCharge_InvalidProduct() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(testWarehouse));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                shippingService.getShippingCharge(1L, 1L, 999L, DeliverySpeed.STANDARD)
        );
    }

    @Test
    @DisplayName("calculateShipping - end-to-end combines warehouse + charge")
    void testCalculateShipping_EndToEnd() {
        WarehouseResponse nearestWh = WarehouseResponse.builder()
                .warehouseId(1L)
                .name("BLR_Warehouse")
                .warehouseLocation(new WarehouseResponse.LocationDto(12.9716, 77.5946))
                .build();
        when(warehouseService.findNearestWarehouse(1L)).thenReturn(nearestWh);

        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(testWarehouse));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(osrmService.getDistanceKm(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(200.0);

        StandardDeliveryStrategy strategy = new StandardDeliveryStrategy();
        when(strategyFactory.getStrategy(DeliverySpeed.STANDARD)).thenReturn(strategy);

        ShippingCalculationRequest request = ShippingCalculationRequest.builder()
                .sellerId(1L)
                .customerId(1L)
                .productId(1L)
                .deliverySpeed(DeliverySpeed.STANDARD)
                .build();

        CombinedShippingResponse response = shippingService.calculateShipping(request);

        assertNotNull(response);
        assertNotNull(response.getNearestWarehouse());
        assertEquals(1L, response.getNearestWarehouse().getWarehouseId());
        assertNotNull(response.getChargeBreakdown());
        assertEquals(response.getShippingCharge(), response.getChargeBreakdown().getShippingCharge());

        verify(warehouseService, times(1)).findNearestWarehouse(1L);
    }
}