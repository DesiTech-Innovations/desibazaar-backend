package com.desitech.vyaparsathi.shop.service;

import com.desitech.vyaparsathi.shop.dto.ShopDto;
import com.desitech.vyaparsathi.shop.entity.Shop;
import com.desitech.vyaparsathi.shop.mapper.ShopMapper;
import com.desitech.vyaparsathi.shop.repository.ShopRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShopService {

    private static final Long SHOP_ID = 1L;

    @Autowired
    private ShopRepository repository;

    @Autowired
    private ShopMapper mapper;

    @Transactional
    public ShopDto createInitialShop(ShopDto dto) {
        if (repository.count() > 0) {
            throw new IllegalStateException("Shop is already set up. Cannot create a new one.");
        }
        Shop shop = mapper.toEntity(dto);
        shop = repository.save(shop);
        return mapper.toDto(shop);
    }

    @Transactional
    public ShopDto updateShop(ShopDto dto) {
        Shop shop = repository.findById(SHOP_ID)
                .orElseThrow(() -> new EntityNotFoundException("Shop not found for update."));

        mapper.updateShopFromDto(dto, shop);
        shop = repository.save(shop);
        return mapper.toDto(shop);
    }

    public ShopDto getShop() {
        return repository.findById(SHOP_ID)
                .map(mapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Shop not found."));
    }
}
