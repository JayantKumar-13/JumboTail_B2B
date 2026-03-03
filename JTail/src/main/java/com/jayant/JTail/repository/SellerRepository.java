package com.jayant.JTail.repository;

import com.jayant.JTail.entity.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface SellerRepository extends JpaRepository<Seller, Long> {

    @Query("SELECT s FROM Seller s WHERE s.user.email = :email")
    Optional<Seller> findByUserEmail(@Param("email") String email);

    boolean existsByGstNumber(String gstNumber);
}
