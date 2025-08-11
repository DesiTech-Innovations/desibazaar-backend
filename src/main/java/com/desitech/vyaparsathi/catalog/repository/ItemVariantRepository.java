package com.desitech.vyaparsathi.catalog.repository;

import com.desitech.vyaparsathi.catalog.entity.ItemVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemVariantRepository extends JpaRepository<ItemVariant, Long> {

    /**
     * Finds an ItemVariant by its unique SKU.
     * @param sku The SKU to search for.
     * @return An Optional containing the ItemVariant if found, or an empty Optional otherwise.
     */
    Optional<ItemVariant> findBySku(String sku);
}