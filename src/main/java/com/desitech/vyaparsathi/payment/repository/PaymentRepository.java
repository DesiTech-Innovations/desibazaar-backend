package com.desitech.vyaparsathi.payment.repository;

import com.desitech.vyaparsathi.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findBySourceTypeAndSourceId(String sourceType, Long sourceId);
    List<Payment> findBySupplierId(Long supplierId);
    List<Payment> findByCustomerId(Long customerId);
}