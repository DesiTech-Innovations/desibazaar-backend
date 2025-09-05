package com.desitech.vyaparsathi.inventory.repository;

import com.desitech.vyaparsathi.inventory.entity.ItemVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemVariantRepository extends JpaRepository<ItemVariant, Long> {

    Optional<ItemVariant> findBySku(String sku);

    boolean existsByHsn(String hsn);

    /**
     * Searches for ItemVariants based on a combination of item and variant attributes.
     * This query is designed to power the main item search/filter functionality.
     */
    @Query("SELECT iv FROM ItemVariant iv JOIN iv.item i LEFT JOIN i.category c WHERE " +
            "(:name IS NULL OR i.name LIKE %:name%) AND " +
            "(:categoryName IS NULL OR c.name LIKE %:categoryName%) AND " +
            "(:color IS NULL OR iv.color LIKE %:color%) AND " +
            "(:size IS NULL OR iv.size LIKE %:size%) AND " +
            "(:design IS NULL OR iv.design LIKE %:design%) AND " +
            "(:sku IS NULL OR iv.sku LIKE %:sku%) AND " +
            "(:fabric IS NULL OR i.fabric LIKE %:fabric%) AND " +
            "(:season IS NULL OR i.season LIKE %:season%) AND " +
            "(:fit IS NULL OR iv.fit LIKE %:fit%)")
    List<ItemVariant> searchVariants(
            @Param("name") String name,
            @Param("categoryName") String categoryName,
            @Param("color") String color,
            @Param("size") String size,
            @Param("design") String design,
            @Param("sku") String sku,
            @Param("fabric") String fabric,
            @Param("season") String season,
            @Param("fit") String fit
    );

    /**
     * NEW: Finds all variants that have a low stock threshold configured.
     * This is used by the LowStockAlerts feature.
     */
    List<ItemVariant> findAllByLowStockThresholdIsNotNull();

}