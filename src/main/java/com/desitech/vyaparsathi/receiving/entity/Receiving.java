package com.desitech.vyaparsathi.receiving.entity;

import com.desitech.vyaparsathi.purchaseorder.entity.PurchaseOrder;
import com.desitech.vyaparsathi.receiving.enums.ReceivingStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
public class Receiving {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @Enumerated(EnumType.STRING)
    private ReceivingStatus status;

    private LocalDateTime receivedAt;
    private String receivedBy;
    private String notes;

    @OneToMany(mappedBy = "receiving", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReceivingItem> items;
}
