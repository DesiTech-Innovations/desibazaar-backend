package com.desitech.vyaparsathi.catalog.controller;

import com.desitech.vyaparsathi.catalog.dto.ItemDto;
import com.desitech.vyaparsathi.catalog.service.CatalogService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    @Autowired
    private CatalogService service;

    @GetMapping
    public List<ItemDto> getAllItems() {
        return service.getAllItems();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getItemById(id));
    }

    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<ItemDto> createItem(
            @Valid @RequestPart("itemDto") ItemDto itemDto,
            @RequestPart("photos") MultipartFile[] photos) {

        if (photos != null && photos.length != itemDto.getVariants().size()) {
            throw new RuntimeException("Number of photos must match number of variants.");
        }

        for (int i = 0; i < itemDto.getVariants().size(); i++) {
            assert photos != null;
            String photoPath = savePhoto(photos[i]);
            itemDto.getVariants().get(i).setPhotoPath(photoPath);
        }

        return ResponseEntity.ok(service.createItem(itemDto));
    }

    private String savePhoto(MultipartFile photo) {
        // Save to disk/cloud and return path
        if (photo != null && !photo.isEmpty()) {
            String fileName = photo.getOriginalFilename();
            Path path = Paths.get("uploads/item-photos/" + fileName);
            try {
                Files.createDirectories(path.getParent());
                Files.write(path, photo.getBytes());
                return path.toString();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
    @PostMapping("/bulk")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<List<ItemDto>> createItems(@Valid @RequestBody List<ItemDto> items) {
        return ResponseEntity.ok(service.createItems(items));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<ItemDto> updateItem(@PathVariable Long id, @Valid @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok(service.updateItem(id, itemDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        service.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}
