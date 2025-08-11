package com.desitech.vyaparsathi.catalog.repository;

import com.desitech.vyaparsathi.catalog.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}