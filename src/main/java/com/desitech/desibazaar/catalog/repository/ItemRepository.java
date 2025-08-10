package com.desitech.desibazaar.catalog.repository;

import com.desitech.desibazaar.catalog.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}