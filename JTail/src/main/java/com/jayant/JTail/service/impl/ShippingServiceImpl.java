package com.jayant.JTail.service.impl;

import com.jayant.JTail.dto.request.ShippingCalculationRequest;
import com.jayant.JTail.dto.response.CombinedShippingResponse;
import com.jayant.JTail.dto.response.ShippingChargeResponse;
import com.jayant.JTail.dto.response.WarehouseResponse;
import com.jayant.JTail.entity.Customer;
import com.jayant.JTail.entity.Product;
import com.jayant.JTail.entity.Warehouse;
import com.jayant.JTail.enums.DeliverySpeed;
import com.jayant.JTail.exception.InvalidRequestException;
import com.jayant.JTail.exception.ResourceNotFoundException;
import com.jayant.JTail.repository.CustomerRepository;
import com.jayant.JTail.repository.ProductRepository;
import com.jayant.JTail.repository.WarehouseRepository;
import com.jayant.JTail.service.interfaces.OSRMService;
import com.jayant.JTail.service.interfaces.ShippingService;
import com.jayant.JTail.service.interfaces.WarehouseService;
import com.jayant.JTail.strategy.DeliveryStrategy;
import com.jayant.JTail.strategy.DeliveryStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Service implementation for shipping-related business logic: calculating shipping charges and combined shipping details.
@Service
@RequiredArgsConstructor
@Slf4j
public class ShippingServiceImpl implements ShippingService {

    private final CustomerRepository    customerRepository;
    private final WarehouseRepository   warehouseRepository;
    private final ProductRepository     productRepository;
    private final OSRMService           osrmService;
    private final DeliveryStrategyFactory strategyFactory;
    private final WarehouseService      warehouseService;

    @Override
    @Cacheable(value = "shippingCharge",
               key = "#warehouseId + '-' + #customerId + '-' + #productId + '-' + #deliverySpeed")
    @Transactional(readOnly = true)
    public ShippingChargeResponse getShippingCharge(Long warehouseId,
                                                     Long customerId,
                                                     Long productId,
                                                     DeliverySpeed deliverySpeed) {

        log.info("Calculating shipping: warehouse={}, customer={}, product={}, speed={}",
                warehouseId, customerId, productId, deliverySpeed);

        // 1. Load entities
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", warehouseId));

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        // 2. Validate locations
        if (warehouse.getLocation() == null) {
            throw new InvalidRequestException(
                    "Warehouse (id=" + warehouseId + ") has no location set.");
        }
        if (customer.getLocation() == null) {
            throw new InvalidRequestException(
                    "Customer (id=" + customerId + ") has no location set.");
        }
        double distanceKm = osrmService.getDistanceKm(
                warehouse.getLocation().getY(),  // warehouse latitude
                warehouse.getLocation().getX(),  // warehouse longitude
                customer.getLocation().getY(),   // customer latitude
                customer.getLocation().getX()    // customer longitude
        );

        log.info("Distance from warehouse {} to customer {}: {} km",
                warehouseId, customerId, String.format("%.2f", distanceKm));

        // 4. Select the delivery strategy and calculate charge
        DeliveryStrategy strategy = strategyFactory.getStrategy(deliverySpeed);
        ShippingChargeResponse response = strategy.calculateCharge(
                distanceKm, product.getWeightKg()
        );

        log.info("Shipping charge calculated: Rs {} (transport: {}, speed: {})",
                response.getShippingCharge(),
                response.getTransportMode(),
                deliverySpeed);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public CombinedShippingResponse calculateShipping(ShippingCalculationRequest request) {
        log.info("End-to-end shipping calculation for seller={}, customer={}, product={}",
                request.getSellerId(), request.getCustomerId(), request.getProductId());

        // 1. Find nearest warehouse to the seller
        WarehouseResponse nearestWarehouse =
                warehouseService.findNearestWarehouse(request.getSellerId());

        // 2. Calculate shipping from that warehouse to the customer
        ShippingChargeResponse chargeResponse = getShippingCharge(
                nearestWarehouse.getWarehouseId(),
                request.getCustomerId(),
                request.getProductId(),
                request.getDeliverySpeed()
        );

        return CombinedShippingResponse.builder()
                .shippingCharge(chargeResponse.getShippingCharge())
                .nearestWarehouse(nearestWarehouse)
                .chargeBreakdown(chargeResponse)
                .build();
    }
}
