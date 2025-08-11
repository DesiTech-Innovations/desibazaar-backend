package com.desitech.vyaparsathi.inventory.service;

import com.desitech.vyaparsathi.catalog.entity.ItemVariant;
import com.desitech.vyaparsathi.catalog.repository.ItemVariantRepository;
import com.desitech.vyaparsathi.inventory.dto.PurchaseOrderDto;
import com.desitech.vyaparsathi.inventory.entity.PurchaseOrder;
import com.desitech.vyaparsathi.inventory.entity.PurchaseOrderItem;
import com.desitech.vyaparsathi.inventory.entity.Supplier;
import com.desitech.vyaparsathi.inventory.mapper.PurchaseOrderMapper;
import com.desitech.vyaparsathi.inventory.repository.PurchaseOrderItemRepository;
import com.desitech.vyaparsathi.inventory.repository.PurchaseOrderRepository;
import com.desitech.vyaparsathi.inventory.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PurchaseService {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private PurchaseOrderItemRepository purchaseOrderItemRepository;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ItemVariantRepository itemVariantRepository;

    @Autowired
    private StockService stockService; // Inject the StockService to update inventory

    @Autowired
    private PurchaseOrderMapper mapper;

    @Transactional
    public PurchaseOrderDto createPurchaseOrder(PurchaseOrderDto dto) {
        Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setPoNumber(dto.getPoNumber());
        purchaseOrder.setSupplier(supplier);
        purchaseOrder.setOrderDate(LocalDateTime.now());
        purchaseOrder.setStatus("Draft");

        BigDecimal totalAmount = dto.getItems().stream()
                .map(item -> item.getUnitCost().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        purchaseOrder.setTotalAmount(totalAmount);

        PurchaseOrder savedPurchaseOrder = purchaseOrderRepository.save(purchaseOrder);

        List<PurchaseOrderItem> items = dto.getItems().stream().map(itemDto -> {
            ItemVariant itemVariant = itemVariantRepository.findById(itemDto.getItemVariantId())
                    .orElseThrow(() -> new RuntimeException("Item Variant not found"));

            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setPurchaseOrder(savedPurchaseOrder);
            item.setItemVariant(itemVariant);
            item.setQuantity(itemDto.getQuantity());
            item.setUnitCost(itemDto.getUnitCost());
            return item;
        }).collect(Collectors.toList());

        purchaseOrderItemRepository.saveAll(items);

        savedPurchaseOrder.setItems(items);
        return mapper.toDto(savedPurchaseOrder);
    }

    public List<PurchaseOrderDto> findAllPurchaseOrders() {
        return purchaseOrderRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public PurchaseOrderDto findPurchaseOrderById(Integer id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found"));
        return mapper.toDto(purchaseOrder);
    }

    @Transactional
    public void receivePurchaseOrder(Integer id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found"));

        if (!"Draft".equals(purchaseOrder.getStatus())) {
            throw new IllegalStateException("Only purchase orders with 'Draft' status can be received.");
        }

        for (PurchaseOrderItem item : purchaseOrder.getItems()) {
            // Call the simplified addStock method
            stockService.addStock(item.getItemVariant().getId(), BigDecimal.valueOf(item.getQuantity()));
        }

        purchaseOrder.setStatus("Received");
        purchaseOrderRepository.save(purchaseOrder);
    }
}