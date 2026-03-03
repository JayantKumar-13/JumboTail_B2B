package com.jayant.JTail.service.impl;

import com.jayant.JTail.dto.request.WarehouseRequest;
import com.jayant.JTail.dto.response.WarehouseResponse;
import com.jayant.JTail.entity.Seller;
import com.jayant.JTail.entity.Warehouse;
import com.jayant.JTail.exception.DuplicateResourceException;
import com.jayant.JTail.exception.ResourceNotFoundException;
import com.jayant.JTail.exception.ShippingCalculationException;
import com.jayant.JTail.repository.SellerRepository;
import com.jayant.JTail.repository.WarehouseRepository;
import com.jayant.JTail.service.interfaces.WarehouseService;
import com.jayant.JTail.utils.GeometryUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final SellerRepository sellerRepository;

    @Override
    @Transactional
    public WarehouseResponse createWarehouse(WarehouseRequest request) {
        if (warehouseRepository.findByName(request.getName()).isPresent()) {
            throw new DuplicateResourceException(
                    "Warehouse with name '" + request.getName() + "' already exists");
        }

        Warehouse warehouse = Warehouse.builder()
                .name(request.getName())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .pincode(request.getPincode())
                .contactPhone(request.getContactPhone())
                .location(GeometryUtil.createPoint(request.getLocation()))
                .capacitySqFt(request.getCapacitySqFt())
                .active(true)
                .build();

        warehouse = warehouseRepository.save(warehouse);
        log.info("Created warehouse: {} (id={})", warehouse.getName(), warehouse.getId());
        return toResponse(warehouse);
    }

    @Override
    @Cacheable(value = "nearestWarehouse", key = "#sellerId")
    @Transactional(readOnly = true)
    public WarehouseResponse findNearestWarehouse(Long sellerId) {
        log.info("Finding nearest warehouse for seller: {}", sellerId);

        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller", "id", sellerId));

        if (seller.getLocation() == null) {
            throw new ShippingCalculationException(
                    "Seller (id=" + sellerId + ") has no location set.");
        }

        double lng = seller.getLocation().getX();
        double lat = seller.getLocation().getY();

        Long nearestId = warehouseRepository
                .findNearestWarehouseId(lng, lat)
                .orElseThrow(() -> new ShippingCalculationException(
                        "No active warehouses found. Please contact support."));

        Warehouse nearest = warehouseRepository.findById(nearestId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", nearestId));

        log.info("Nearest warehouse for seller {}: {} (id={})",
                sellerId, nearest.getName(), nearest.getId());

        return toResponse(nearest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WarehouseResponse> getAllWarehouses() {
        return warehouseRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public WarehouseResponse getWarehouseById(Long warehouseId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", warehouseId));
        return toResponse(warehouse);
    }

    public WarehouseResponse toResponse(Warehouse warehouse) {
        return WarehouseResponse.builder()
                .warehouseId(warehouse.getId())
                .name(warehouse.getName())
                .address(warehouse.getAddress())
                .city(warehouse.getCity())
                .warehouseLocation(WarehouseResponse.LocationDto.builder()
                        .lat(warehouse.getLocation().getY())
                        .lng(warehouse.getLocation().getX())
                        .build())
                .build();
    }
}