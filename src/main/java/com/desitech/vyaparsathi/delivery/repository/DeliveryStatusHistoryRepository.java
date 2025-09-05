package com.desitech.vyaparsathi.delivery.repository;

import com.desitech.vyaparsathi.delivery.entity.DeliveryStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DeliveryStatusHistoryRepository extends JpaRepository<DeliveryStatusHistory, Long> {
    List<DeliveryStatusHistory> findByDelivery_DeliveryId(Long deliveryId);
}