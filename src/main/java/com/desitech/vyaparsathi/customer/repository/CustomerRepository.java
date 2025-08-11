package com.desitech.vyaparsathi.customer.repository;

import com.desitech.vyaparsathi.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByNameContainingIgnoreCase(String name);
}
