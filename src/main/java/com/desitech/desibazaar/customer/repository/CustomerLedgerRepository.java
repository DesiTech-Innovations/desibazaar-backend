package com.desitech.desibazaar.customer.repository;

import com.desitech.desibazaar.customer.entity.Customer;
import com.desitech.desibazaar.customer.entity.CustomerLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CustomerLedgerRepository extends JpaRepository<CustomerLedger, Long> {
    List<CustomerLedger> findByCustomerOrderByCreatedAtDesc(Customer customer);
}
