package com.jayant.JTail.service.interfaces;

import com.jayant.JTail.dto.request.WarehouseRequest;
import com.jayant.JTail.dto.response.WarehouseResponse;

import java.util.List;

public interface WarehouseService {

    WarehouseResponse createWarehouse(WarehouseRequest request);

    WarehouseResponse findNearestWarehouse(Long sellerId);

    List<WarehouseResponse> getAllWarehouses();

    WarehouseResponse getWarehouseById(Long warehouseId);
}