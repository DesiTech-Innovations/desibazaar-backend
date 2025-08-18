package com.desitech.vyaparsathi.catalog.repository;

import com.desitech.vyaparsathi.catalog.entity.ItemVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemVariantRepository extends JpaRepository<ItemVariant, Long> {

    /**
     * Finds an ItemVariant by its unique SKU.
     * @param sku The SKU to search for.
     * @return An Optional containing the ItemVariant if found, or an empty Optional otherwise.
     */
    Optional<ItemVariant> findBySku(String sku);

    /**
     * Searches for ItemVariants based on item name, category, color, size, and design.
     * Joins with the Item entity to access name and category.
     * @param name The item name to filter by (nullable).
     * @param category The category to filter by (nullable).
     * @param color The color to filter by (nullable).
     * @param size The size to filter by (nullable).
     * @param design The design to filter by (nullable).
     * @return A list of matching ItemVariants.
     */
    @Query("SELECT iv FROM ItemVariant iv JOIN iv.item i WHERE (:name IS NULL OR i.name LIKE %:name%) AND (:category IS NULL OR i.category LIKE %:category%) AND (:color IS NULL OR iv.color LIKE %:color%) AND (:size IS NULL OR iv.size LIKE %:size%) AND (:design IS NULL OR iv.design LIKE %:design%) AND (:sku IS NULL OR iv.sku LIKE %:sku%)")
    List<ItemVariant> searchVariants(
            @Param("name") String name,
            @Param("category") String category,
            @Param("color") String color,
            @Param("size") String size,
            @Param("design") String design,
            @Param("sku") String sku
    );
}