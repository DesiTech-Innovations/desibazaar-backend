package com.desitech.vyaparsathi.inventory.repository;

import com.desitech.vyaparsathi.inventory.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
	// Add custom fetch if Supplier has relations in future (e.g., @EntityGraph)
}
