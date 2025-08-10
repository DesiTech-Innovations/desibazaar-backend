package com.desitech.desibazaar.shop.service;

import com.desitech.desibazaar.shop.dto.ShopDto;
import com.desitech.desibazaar.shop.entity.Shop;
import com.desitech.desibazaar.shop.mapper.ShopMapper;
import com.desitech.desibazaar.shop.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ShopService {
    @Autowired
    private ShopRepository repository;
    @Autowired
    private ShopMapper mapper;

    public ShopDto setup(ShopDto dto) {
        // Check if shop already exists (initial bootstrap only)
        if (repository.count() > 0) {
            throw new RuntimeException("Shop already set up");
        }
        Shop shop = mapper.toEntity(dto);
        repository.save(shop);
        return mapper.toDto(shop);
    }

    public ShopDto getShop() {
        Optional<Shop> shop = repository.findById(1L);  // Assume single shop
        return shop.map(mapper::toDto).orElseThrow(() -> new RuntimeException("Shop not found"));
    }
}