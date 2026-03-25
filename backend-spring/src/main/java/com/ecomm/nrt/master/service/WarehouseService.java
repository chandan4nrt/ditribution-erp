package com.ecomm.nrt.master.service;

import com.ecomm.nrt.master.dto.WarehouseRequest;
import com.ecomm.nrt.master.dto.WarehouseResponse;
import com.ecomm.nrt.master.entity.Warehouse;
import com.ecomm.nrt.master.repository.WarehouseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final ModelMapper modelMapper;

    public List<WarehouseResponse> getAll() {
        return warehouseRepository.findByIsActiveTrue().stream()
                .map(w -> modelMapper.map(w, WarehouseResponse.class))
                .toList();
    }

    public WarehouseResponse getById(Long id) {
        return modelMapper.map(findOrThrow(id), WarehouseResponse.class);
    }

    @Transactional
    @SuppressWarnings("null")
    public WarehouseResponse create(WarehouseRequest request) {
        if (warehouseRepository.existsByCode(request.getCode()))
            throw new IllegalArgumentException("Warehouse code already exists: " + request.getCode());
        Warehouse warehouse = modelMapper.map(request, Warehouse.class);
        warehouse.setIsActive(true);
        return modelMapper.map(warehouseRepository.save(warehouse), WarehouseResponse.class);
    }

    @Transactional
    @SuppressWarnings("null")
    public WarehouseResponse update(Long id, WarehouseRequest request) {
        Warehouse warehouse = findOrThrow(id);
        modelMapper.map(request, warehouse);
        return modelMapper.map(warehouseRepository.save(warehouse), WarehouseResponse.class);
    }

    @Transactional
    @SuppressWarnings("null")
    public void deactivate(Long id) {
        Warehouse warehouse = findOrThrow(id);
        warehouse.setIsActive(false);
        warehouseRepository.save(warehouse);
    }

    private Warehouse findOrThrow(Long id) {
        if (id == null) throw new IllegalArgumentException("Warehouse ID must not be null");
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Warehouse not found with id: " + id));
    }
}
