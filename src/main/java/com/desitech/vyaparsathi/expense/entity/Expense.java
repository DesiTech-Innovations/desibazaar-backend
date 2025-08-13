package com.desitech.vyaparsathi.expense.entity;

import com.desitech.vyaparsathi.common.util.LocalDateTimeAttributeConverter;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "expense")
@Data
@NoArgsConstructor
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shop_id", nullable = false)
    private Long shopId;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime date;

    private String notes;

    private boolean deleted = false;

    @PrePersist
    @Convert(converter = LocalDateTimeAttributeConverter.class)
    public void onCreate() {
        if (this.date == null) {
            this.date = LocalDateTime.now();
        }
    }
}