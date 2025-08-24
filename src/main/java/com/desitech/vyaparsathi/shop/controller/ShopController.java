
package com.desitech.vyaparsathi.shop.controller;
import com.desitech.vyaparsathi.common.exception.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desitech.vyaparsathi.shop.dto.ShopDto;
import com.desitech.vyaparsathi.shop.service.ShopService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shop")
public class ShopController {

    private static final Logger logger = LoggerFactory.getLogger(ShopController.class);

    @Autowired
    private ShopService service;

    /**
     * Endpoint to set up the initial shop.
     * Accessible only to the 'OWNER' role.
     * @param dto The DTO containing shop details.
     * @return The created ShopDto.
     */
    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ShopDto> createInitialShop(@Valid @RequestBody ShopDto dto) {
        try {
            ShopDto result = service.createInitialShop(dto);
            logger.info("Created initial shop with name={}", dto.getName());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error creating initial shop with name={}: {}", dto.getName(), e.getMessage(), e);
            throw new ApplicationException("Failed to create initial shop", e);
        }
    }

    /**
     * Endpoint to update an existing shop's details.
     * Accessible only to the 'OWNER' role.
     * @param dto The DTO containing updated shop details.
     * @return The updated ShopDto.
     */
    @PutMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ShopDto> updateShop(@Valid @RequestBody ShopDto dto) {
        try {
            ShopDto result = service.updateShop(dto);
            logger.info("Updated shop with name={}", dto.getName());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error updating shop with name={}: {}", dto.getName(), e.getMessage(), e);
            throw new ApplicationException("Failed to update shop", e);
        }
    }

    /**
     * Endpoint to retrieve the shop details.
     * Accessible to any authenticated user.
     * @return The ShopDto for the current shop.
     */
    @GetMapping
    public ResponseEntity<ShopDto> getShop() {
        try {
            ShopDto result = service.getShop();
            logger.info("Fetched shop details");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Error fetching shop details: {}", e.getMessage(), e);
            throw new ApplicationException("Failed to fetch shop details", e);
        }
    }
}