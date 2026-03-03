package com.jayant.JTail.repository;

import com.jayant.JTail.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("SELECT c FROM Customer c WHERE c.user.email = :email")
    Optional<Customer> findByUserEmail(@Param("email") String email);

    boolean existsByPhone(String phone);
}