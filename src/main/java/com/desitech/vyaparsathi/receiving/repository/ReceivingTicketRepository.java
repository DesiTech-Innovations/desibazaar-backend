package com.desitech.vyaparsathi.receiving.repository;

import com.desitech.vyaparsathi.receiving.entity.ReceivingTicket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceivingTicketRepository extends JpaRepository<ReceivingTicket, Long> {
}
