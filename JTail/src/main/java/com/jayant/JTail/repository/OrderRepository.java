package com.jayant.JTail.repository;

import com.jayant.JTail.entity.Order;
import com.jayant.JTail.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    List<Order> findByProductSellerIdOrderByCreatedAtDesc(Long sellerId);

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByCustomerIdAndStatus(Long customerId, OrderStatus status);
}
