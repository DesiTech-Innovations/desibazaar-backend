package com.desitech.vyaparsathi.customer.service;

import com.desitech.vyaparsathi.customer.dto.CustomerDto;
import com.desitech.vyaparsathi.customer.entity.Customer;
import com.desitech.vyaparsathi.customer.mapper.CustomerMapper;
import com.desitech.vyaparsathi.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CustomerMapper mapper;

    @Transactional
    public CustomerDto addCustomer(CustomerDto dto) {
        Customer customer = mapper.toEntity(dto);
        if (customer.getCreditBalance() == null) {
            customer.setCreditBalance(BigDecimal.ZERO);
        }
        customerRepository.save(customer);
        return mapper.toDto(customer);
    }

    @Transactional
    public CustomerDto updateCustomer(Long id, CustomerDto dto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

    mapper.updateEntityFromDto(dto, customer);

        customerRepository.save(customer);
        return mapper.toDto(customer);
    }

    public List<CustomerDto> listCustomers() {
        return customerRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public Optional<CustomerDto> getCustomer(Long id) {
        return customerRepository.findById(id).map(mapper::toDto);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    public List<CustomerDto> searchCustomers(String name) {
        return customerRepository.findByNameContainingIgnoreCase(name).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}