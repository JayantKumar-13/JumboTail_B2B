package com.jayant.JTail.entity;

import com.jayant.JTail.enums.DeliverySpeed;
import com.jayant.JTail.enums.OrderStatus;
import com.jayant.JTail.enums.TransportMode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Entity representing a customer order, linking the customer, product, and warehouse, and storing details about the order such as quantity, pricing, delivery preferences, and current status in the order lifecycle.
@Entity
@Table(name = "orders")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private Warehouse warehouse;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal productPriceAtOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliverySpeed deliverySpeed;

    @Enumerated(EnumType.STRING)
    private TransportMode transportMode;

    @Column
    private Double distanceKm;

    @Column(precision = 10, scale = 2)
    private BigDecimal shippingCharge;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(length = 300)
    private String deliveryAddress;

    @Column
    private LocalDateTime estimatedDeliveryAt;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
