package com.desitech.vyaparsathi.inventory.service;

import com.desitech.vyaparsathi.inventory.dto.SupplierDto;
import com.desitech.vyaparsathi.inventory.entity.Supplier;
import com.desitech.vyaparsathi.inventory.mapper.SupplierMapper;
import com.desitech.vyaparsathi.inventory.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupplierService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private SupplierMapper mapper;

    public SupplierDto createSupplier(SupplierDto dto) {
        Supplier supplier = mapper.toEntity(dto);
        supplierRepository.save(supplier);
        return mapper.toDto(supplier);
    }

    public List<SupplierDto> findAllSuppliers() {
        // Avoid N+1 by fetching all suppliers in one query (no relations to fetch here)
        return supplierRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public SupplierDto findSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        return mapper.toDto(supplier);
    }

    public SupplierDto updateSupplier(Long id, SupplierDto dto) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        // Update fields
        supplier.setName(dto.getName());
        supplier.setContactPerson(dto.getContactPerson());
        supplier.setPhone(dto.getPhone());
        supplier.setEmail(dto.getEmail());
        supplier.setAddress(dto.getAddress());
        supplier.setGstin(dto.getGstin());
        supplierRepository.save(supplier);
        return mapper.toDto(supplier);
    }

    public void deleteSupplier(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new RuntimeException("Supplier not found");
        }
        supplierRepository.deleteById(id);
    }
}