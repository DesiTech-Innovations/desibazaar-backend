package com.desitech.desibazaar.shop.repository;

import com.desitech.desibazaar.shop.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository extends JpaRepository<Shop, Long> {
}