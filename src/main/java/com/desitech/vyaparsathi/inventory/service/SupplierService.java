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
        return supplierRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public SupplierDto findSupplierById(Integer id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        return mapper.toDto(supplier);
    }
}