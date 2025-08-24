package com.desitech.vyaparsathi.purchaseorder.service;

import com.desitech.vyaparsathi.inventory.entity.ItemVariant;
import com.desitech.vyaparsathi.inventory.repository.ItemVariantRepository;
import com.desitech.vyaparsathi.purchaseorder.dto.PurchaseOrderDto;
import com.desitech.vyaparsathi.purchaseorder.entity.PurchaseOrder;
import com.desitech.vyaparsathi.purchaseorder.entity.PurchaseOrderItem;
import com.desitech.vyaparsathi.inventory.entity.Supplier;
import com.desitech.vyaparsathi.purchaseorder.mapper.PurchaseOrderMapper;
import com.desitech.vyaparsathi.purchaseorder.repository.PurchaseOrderItemRepository;
import com.desitech.vyaparsathi.purchaseorder.repository.PurchaseOrderRepository;
import com.desitech.vyaparsathi.inventory.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PurchaseOrderService {
    private static final Logger logger = LoggerFactory.getLogger(PurchaseOrderService.class);
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    private PurchaseOrderItemRepository purchaseOrderItemRepository;
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private ItemVariantRepository itemVariantRepository;
    @Autowired
    private com.desitech.vyaparsathi.inventory.service.StockService stockService;
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
        purchaseOrder.setStatus(dto.getStatus());
        purchaseOrder.setNotes(dto.getNotes());
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

    public PurchaseOrderDto findPurchaseOrderById(Long id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found"));
        return mapper.toDto(purchaseOrder);
    }

    @Transactional
    public void receivePurchaseOrder(Long id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found"));
        if (!"PENDING".equals(purchaseOrder.getStatus())) {
            throw new IllegalStateException("Only purchase orders with 'Draft' status can be received.");
        }
        for (PurchaseOrderItem item : purchaseOrder.getItems()) {
            stockService.addStock(
                item.getItemVariant().getId(), 
                BigDecimal.valueOf(item.getQuantity()),
                item.getUnitCost(),
                "Purchase Order #" + purchaseOrder.getPoNumber()
            );
        }
        purchaseOrder.setStatus(purchaseOrder.getStatus());
        purchaseOrderRepository.save(purchaseOrder);
    }

    @Transactional
    public PurchaseOrderDto updatePurchaseOrder(Long id, PurchaseOrderDto dto) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found"));
        Supplier supplier = supplierRepository.findById(dto.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        purchaseOrder.setSupplier(supplier);
        purchaseOrder.setOrderDate(dto.getOrderDate() != null ? dto.getOrderDate() : purchaseOrder.getOrderDate());
        purchaseOrder.setExpectedDeliveryDate(dto.getExpectedDeliveryDate() != null ? dto.getExpectedDeliveryDate() : purchaseOrder.getExpectedDeliveryDate());
        purchaseOrder.setNotes(dto.getNotes());
        purchaseOrder.setStatus(dto.getStatus());


        // Remove old items and add new items (let JPA handle orphan removal)
        if (purchaseOrder.getItems() != null) {
            purchaseOrder.getItems().clear();
        }
        List<PurchaseOrderItem> newItems = dto.getItems().stream().map(itemDto -> {
            ItemVariant itemVariant = itemVariantRepository.findById(itemDto.getItemVariantId())
                    .orElseThrow(() -> new RuntimeException("Item Variant not found"));
            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setPurchaseOrder(purchaseOrder);
            item.setItemVariant(itemVariant);
            item.setQuantity(itemDto.getQuantity());
            item.setUnitCost(itemDto.getUnitCost());
            return item;
        }).collect(Collectors.toList());
        if (purchaseOrder.getItems() != null) {
            purchaseOrder.getItems().addAll(newItems);
        } else {
            purchaseOrder.setItems(newItems);
        }

        // Update total amount
        BigDecimal totalAmount = newItems.stream()
                .map(item -> item.getUnitCost().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        purchaseOrder.setTotalAmount(totalAmount);

        PurchaseOrder saved = purchaseOrderRepository.save(purchaseOrder);
        return mapper.toDto(saved);
    }

}
