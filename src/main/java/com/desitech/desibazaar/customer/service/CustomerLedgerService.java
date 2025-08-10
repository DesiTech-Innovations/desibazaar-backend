package com.desitech.desibazaar.customer.service;

import com.desitech.desibazaar.customer.entity.Customer;
import com.desitech.desibazaar.customer.entity.CustomerLedger;
import com.desitech.desibazaar.customer.repository.CustomerLedgerRepository;
import com.desitech.desibazaar.customer.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerLedgerService {
    private final CustomerLedgerRepository ledgerRepository;
    private final CustomerRepository customerRepository;

    public CustomerLedgerService(CustomerLedgerRepository ledgerRepository,
                                 CustomerRepository customerRepository) {
        this.ledgerRepository = ledgerRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public CustomerLedger addEntry(Long customerId, Double amount, String type, String description) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        if (!type.equalsIgnoreCase("CREDIT") && !type.equalsIgnoreCase("DEBIT")) {
            throw new RuntimeException("Invalid ledger type. Use CREDIT or DEBIT.");
        }

        // Adjust credit balance
        if (type.equalsIgnoreCase("CREDIT")) {
            customer.setCreditBalance(customer.getCreditBalance() + amount);
        } else { // DEBIT
            customer.setCreditBalance(customer.getCreditBalance() - amount);
        }
        customerRepository.save(customer);

        // Save ledger entry
        CustomerLedger ledger = new CustomerLedger();
        ledger.setCustomer(customer);
        ledger.setAmount(amount);
        ledger.setType(type.toUpperCase());
        ledger.setDescription(description);

        return ledgerRepository.save(ledger);
    }

    public List<CustomerLedger> getLedger(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return ledgerRepository.findByCustomerOrderByCreatedAtDesc(customer);
    }
}
