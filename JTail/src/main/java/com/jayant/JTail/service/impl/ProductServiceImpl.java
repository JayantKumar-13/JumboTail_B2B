package com.jayant.JTail.service.impl;

import com.jayant.JTail.dto.request.ProductRequest;
import com.jayant.JTail.dto.response.ProductResponse;
import com.jayant.JTail.entity.Product;
import com.jayant.JTail.entity.Seller;
import com.jayant.JTail.exception.DuplicateResourceException;
import com.jayant.JTail.exception.InvalidRequestException;
import com.jayant.JTail.exception.ResourceNotFoundException;
import com.jayant.JTail.repository.ProductRepository;
import com.jayant.JTail.repository.SellerRepository;
import com.jayant.JTail.service.interfaces.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// Service implementation for product-related business logic: adding, retrieving, updating, and deactivating products.
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final SellerRepository  sellerRepository;

    @Override
    @Transactional
    @CacheEvict(value = "products", allEntries = true)  
    public ProductResponse addProduct(Long sellerId, ProductRequest request) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller", "id", sellerId));

        
        if (request.getSku() != null && productRepository.existsBySku(request.getSku())) {
            throw new DuplicateResourceException("SKU already exists: " + request.getSku());
        }

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .sellingPrice(request.getSellingPrice())
                .weightKg(request.getWeightKg())
                .lengthCm(request.getLengthCm())
                .widthCm(request.getWidthCm())
                .heightCm(request.getHeightCm())
                .minOrderQuantity(request.getMinOrderQuantity() != null ? request.getMinOrderQuantity() : 1)
                .stockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : 0)
                .category(request.getCategory())
                .sku(request.getSku())
                .active(true)
                .seller(seller)
                .build();

        product = productRepository.save(product);
        log.info("Product added: {} by seller {}", product.getName(), sellerId);
        return toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        return toResponse(product);
    }

    @Override
    @Cacheable(value = "products", key = "'all-active'")
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllActiveProducts() {
        return productRepository.findAllByActiveTrue().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsBySeller(Long sellerId) {
        
        if (!sellerRepository.existsById(sellerId)) {
            throw new ResourceNotFoundException("Seller", "id", sellerId);
        }
        return productRepository.findBySellerIdAndActiveTrue(sellerId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public ProductResponse updateProduct(Long productId, Long sellerId, ProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (!product.getSeller().getId().equals(sellerId)) {
            throw new InvalidRequestException("You are not authorized to update this product");
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setSellingPrice(request.getSellingPrice());
        product.setWeightKg(request.getWeightKg());
        product.setLengthCm(request.getLengthCm());
        product.setWidthCm(request.getWidthCm());
        product.setHeightCm(request.getHeightCm());
        if (request.getMinOrderQuantity() != null) product.setMinOrderQuantity(request.getMinOrderQuantity());
        if (request.getStockQuantity()    != null) product.setStockQuantity(request.getStockQuantity());
        if (request.getCategory()         != null) product.setCategory(request.getCategory());

        product = productRepository.save(product);
        return toResponse(product);
    }

    @Override
    @Transactional
    @CacheEvict(value = "products", allEntries = true)
    public void deactivateProduct(Long productId, Long sellerId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (!product.getSeller().getId().equals(sellerId)) {
            throw new InvalidRequestException("You are not authorized to deactivate this product");
        }

        product.setActive(false);
        productRepository.save(product);
        log.info("Product {} deactivated by seller {}", productId, sellerId);
    }

    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .sellingPrice(product.getSellingPrice())
                .weightKg(product.getWeightKg())
                .lengthCm(product.getLengthCm())
                .widthCm(product.getWidthCm())
                .heightCm(product.getHeightCm())
                .minOrderQuantity(product.getMinOrderQuantity())
                .stockQuantity(product.getStockQuantity())
                .category(product.getCategory())
                .sku(product.getSku())
                .active(product.isActive())
                .sellerId(product.getSeller().getId())
                .sellerName(product.getSeller().getBusinessName())
                .build();
    }
}
