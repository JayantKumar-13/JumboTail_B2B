package com.jayant.JTail.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Entity representing a product listed by a seller, with fields for product details such as name, description, price, weight, dimensions, stock quantity, and category. Each product is associated with a seller and has timestamps for creation and updates.
@Entity
@Table(name = "products")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;
    @Column(length = 500)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal sellingPrice;

    
    @Column(nullable = false)
    private Double weightKg;

    @Column(nullable = false)
    private Double lengthCm;

    @Column(nullable = false)
    private Double widthCm;

    @Column(nullable = false)
    private Double heightCm;

    @Column(nullable = false)
    private Integer minOrderQuantity = 1;

    @Column(nullable = false)
    private Integer stockQuantity = 0;

    @Column(length = 100)
    private String category;

    @Column(length = 50, unique = true)
    private String sku;

    @Column(nullable = false)
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
