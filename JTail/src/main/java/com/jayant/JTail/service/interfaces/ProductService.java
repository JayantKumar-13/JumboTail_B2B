package com.jayant.JTail.service.interfaces;

import com.jayant.JTail.dto.request.ProductRequest;
import com.jayant.JTail.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {

    ProductResponse addProduct(Long sellerId, ProductRequest request);

    ProductResponse getProductById(Long productId);

    List<ProductResponse> getAllActiveProducts();

    List<ProductResponse> getProductsBySeller(Long sellerId);

    ProductResponse updateProduct(Long productId, Long sellerId, ProductRequest request);

    void deactivateProduct(Long productId, Long sellerId);
}