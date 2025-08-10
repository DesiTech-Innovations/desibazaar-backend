package com.desitech.desibazaar.customer.service;

import com.desitech.desibazaar.customer.entity.Customer;
import com.desitech.desibazaar.customer.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer addCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public Customer updateCustomer(Long id, Customer updated) {
        return customerRepository.findById(id).map(c -> {
            c.setName(updated.getName());
            c.setPhone(updated.getPhone());
            c.setEmail(updated.getEmail());
            c.setAddressLine1(updated.getAddressLine1());
            c.setAddressLine2(updated.getAddressLine2());
            c.setCity(updated.getCity());
            c.setState(updated.getState());
            c.setPostalCode(updated.getPostalCode());
            c.setCountry(updated.getCountry());
            c.setGstNumber(updated.getGstNumber());
            c.setPanNumber(updated.getPanNumber());
            c.setNotes(updated.getNotes());
            return customerRepository.save(c);
        }).orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    public List<Customer> listCustomers() {
        return customerRepository.findAll();
    }

    public Optional<Customer> getCustomer(Long id) {
        return customerRepository.findById(id);
    }

    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }
}
