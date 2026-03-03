package com.jayant.JTail.repository;

import com.jayant.JTail.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

    
    @Query(value = """
            SELECT w.id FROM warehouses w
            WHERE w.active = true
            ORDER BY ST_Distance(
                CAST(w.location AS geography),
                CAST(ST_SetSRID(ST_MakePoint(:lng, :lat), 4326) AS geography)
            ) ASC
            LIMIT 1
            """, nativeQuery = true)
    Optional<Long> findNearestWarehouseId(@Param("lng") double lng,
                                          @Param("lat") double lat);

    @Query(value = """
            SELECT w.id FROM warehouses w
            WHERE w.active = true
            ORDER BY ST_Distance(
                CAST(w.location AS geography),
                CAST(ST_SetSRID(ST_MakePoint(:lng, :lat), 4326) AS geography)
            ) ASC
            """, nativeQuery = true)
    List<Long> findAllActiveIdsOrderedByDistance(@Param("lng") double lng,
                                                 @Param("lat") double lat);

    Optional<Warehouse> findByName(String name);

    List<Warehouse> findAllByActiveTrue();
}