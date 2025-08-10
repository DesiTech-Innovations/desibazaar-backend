package com.desitech.desibazaar.shop.controller;

import com.desitech.desibazaar.shop.dto.ShopDto;
import com.desitech.desibazaar.shop.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shop")
public class ShopController {
    @Autowired
    private ShopService service;

    @PostMapping("/setup")
    @PreAuthorize("hasRole('OWNER')")  // Secure; adjust if initial is open
    public ResponseEntity<ShopDto> setup(@RequestBody ShopDto dto) {
        return ResponseEntity.ok(service.setup(dto));
    }

    @GetMapping
    public ResponseEntity<ShopDto> getShop() {
        return ResponseEntity.ok(service.getShop());
    }
}