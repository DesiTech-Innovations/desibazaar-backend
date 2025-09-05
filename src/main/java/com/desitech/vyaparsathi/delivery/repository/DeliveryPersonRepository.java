package com.desitech.vyaparsathi.delivery.repository;

import com.desitech.vyaparsathi.delivery.entity.DeliveryPerson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryPersonRepository extends JpaRepository<DeliveryPerson, Long> { }