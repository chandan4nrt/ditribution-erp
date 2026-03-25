package com.ecomm.nrt.master.service;

import com.ecomm.nrt.master.dto.SupplierRequest;
import com.ecomm.nrt.master.dto.SupplierResponse;
import com.ecomm.nrt.master.entity.Supplier;
import com.ecomm.nrt.master.repository.SupplierRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final ModelMapper modelMapper;

    public Page<SupplierResponse> getAll(String search, Pageable pageable) {
        Page<Supplier> page = (search != null && !search.isBlank())
                ? supplierRepository.searchActive(search, pageable)
                : supplierRepository.findByIsActiveTrue(pageable);
        return page.map(s -> modelMapper.map(s, SupplierResponse.class));
    }

    public SupplierResponse getById(Long id) {
        return modelMapper.map(findOrThrow(id), SupplierResponse.class);
    }

    public SupplierResponse getByCode(String code) {
        Supplier s = supplierRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found: " + code));
        return modelMapper.map(s, SupplierResponse.class);
    }

    @Transactional
    @SuppressWarnings("null")
    public SupplierResponse create(SupplierRequest request) {
        if (supplierRepository.existsByCode(request.getCode()))
            throw new IllegalArgumentException("Supplier code already exists: " + request.getCode());
        Supplier supplier = modelMapper.map(request, Supplier.class);
        supplier.setIsActive(true);
        return modelMapper.map(supplierRepository.save(supplier), SupplierResponse.class);
    }

    @Transactional
    @SuppressWarnings("null")
    public SupplierResponse update(Long id, SupplierRequest request) {
        Supplier supplier = findOrThrow(id);
        modelMapper.map(request, supplier);
        return modelMapper.map(supplierRepository.save(supplier), SupplierResponse.class);
    }

    @Transactional
    @SuppressWarnings("null")
    public void deactivate(Long id) {
        Supplier supplier = findOrThrow(id);
        supplier.setIsActive(false);
        supplierRepository.save(supplier);
    }

    private Supplier findOrThrow(Long id) {
        if (id == null) throw new IllegalArgumentException("Supplier ID must not be null");
        return supplierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Supplier not found with id: " + id));
    }
}
