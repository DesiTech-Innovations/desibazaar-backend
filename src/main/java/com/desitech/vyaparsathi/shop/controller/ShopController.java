package com.desitech.vyaparsathi.shop.controller;

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
        return ResponseEntity.ok(service.createInitialShop(dto));
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
        return ResponseEntity.ok(service.updateShop(dto));
    }

    /**
     * Endpoint to retrieve the shop details.
     * Accessible to any authenticated user.
     * @return The ShopDto for the current shop.
     */
    @GetMapping
    public ResponseEntity<ShopDto> getShop() {
        return ResponseEntity.ok(service.getShop());
    }
}