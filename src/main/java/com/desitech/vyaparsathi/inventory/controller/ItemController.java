
package com.desitech.vyaparsathi.inventory.controller;
import com.desitech.vyaparsathi.common.exception.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.desitech.vyaparsathi.inventory.dto.ItemDto;
import com.desitech.vyaparsathi.inventory.service.ItemService;
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
public class ItemController {

    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);

    @Autowired
    private ItemService itemService;

    @GetMapping
    public List<ItemDto> getAllItems() {
        try {
            List<ItemDto> items = itemService.getAllItems();
            logger.info("Fetched all catalog items");
            return items;
        } catch (Exception e) {
            logger.error("Error fetching all catalog items: {}", e.getMessage(), e);
            throw new ApplicationException("Failed to fetch catalog items", e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable Long id) {
        try {
            ItemDto item = itemService.getItemById(id);
            logger.info("Fetched catalog item id={}", id);
            return ResponseEntity.ok(item);
        } catch (Exception e) {
            logger.error("Error fetching catalog item id={}: {}", id, e.getMessage(), e);
            throw new ApplicationException("Failed to fetch catalog item", e);
        }
    }

    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<ItemDto> createItem(
            @Valid @RequestPart("itemDto") ItemDto itemDto,
            @RequestPart(value = "photos", required = false) MultipartFile[] photos) {

        try {
            // If photos are provided, validate count and assign paths
            if (photos != null && photos.length > 0) {
                if (photos.length != itemDto.getVariants().size()) {
                    logger.warn("Photo count does not match variant count: photos={}, variants={}", photos.length, itemDto.getVariants().size());
                    throw new ApplicationException("Number of photos must match number of variants.");
                }
                for (int i = 0; i < itemDto.getVariants().size(); i++) {
                    String photoPath = savePhoto(photos[i]);
                    itemDto.getVariants().get(i).setPhotoPath(photoPath);
                }
            } // else: No photos provided, do not set photoPath, allow blank

            ItemDto created = itemService.createItem(itemDto);
            logger.info("Created catalog item with name={}", itemDto.getName());
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            logger.error("Error creating catalog item: {}", e.getMessage(), e);
            throw new ApplicationException("Failed to create catalog item", e);
        }
    }
    private String savePhoto(MultipartFile photo) {
        // Save to disk/cloud and return path
        if (photo != null && !photo.isEmpty()) {
            String fileName = photo.getOriginalFilename();
            Path path = Paths.get("uploads/item-photos/" + fileName);
            try {
                Files.createDirectories(path.getParent());
                Files.write(path, photo.getBytes());
                logger.info("Saved photo {} to {}", fileName, path);
                return path.toString();
            } catch (IOException e) {
                logger.error("Error saving photo {}: {}", fileName, e.getMessage(), e);
                throw new ApplicationException("Failed to save photo", e);
            }
        }
        return null;
    }
    @PostMapping("/bulk")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<List<ItemDto>> createItems(@Valid @RequestBody List<ItemDto> items) {
        try {
            List<ItemDto> created = itemService.createItems(items);
            logger.info("Bulk created catalog items, count={}", created.size());
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            logger.error("Error bulk creating catalog items: {}", e.getMessage(), e);
            throw new ApplicationException("Failed to bulk create catalog items", e);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<ItemDto> updateItem(@PathVariable Long id, @Valid @RequestBody ItemDto itemDto) {
        try {
            ItemDto updated = itemService.updateItem(id, itemDto);
            logger.info("Updated catalog item id={}", id);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            logger.error("Error updating catalog item id={}: {}", id, e.getMessage(), e);
            throw new ApplicationException("Failed to update catalog item", e);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        try {
            itemService.deleteItem(id);
            logger.info("Deleted catalog item id={}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting catalog item id={}: {}", id, e.getMessage(), e);
            throw new ApplicationException("Failed to delete catalog item", e);
        }
    }
}
