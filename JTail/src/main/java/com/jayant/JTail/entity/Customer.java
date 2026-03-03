package com.jayant.JTail.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;

// Entity representing a customer in the system, with fields for store details, contact information, location coordinates for distance calculations, and a link to the associated user account.
@Entity
@Table(name = "customers")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String storeName;

    @Column(nullable = false, length = 100)
    private String ownerName;

    @Column(nullable = false, length = 15)
    private String phone;

    @Column(nullable = false, length = 300)
    private String address;
    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(length = 10)
    private String pincode;

    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point location;
    @Column(length = 20)
    private String gstNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
