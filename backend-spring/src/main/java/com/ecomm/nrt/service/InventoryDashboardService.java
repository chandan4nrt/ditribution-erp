package com.ecomm.nrt.service;

import com.ecomm.nrt.entity.WarehouseStock;
import com.ecomm.nrt.master.dto.ProductMasterResponse;
import com.ecomm.nrt.master.repository.ProductMasterRepository;
import com.ecomm.nrt.repository.WarehouseStockRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryDashboardService {

    private final ProductMasterRepository productRepository;
    private final WarehouseStockRepository whStockRepository;
    private final ModelMapper modelMapper;

    public List<ProductMasterResponse> getGlobalInventory() {
        return productRepository.findAll().stream()
                .map(p -> modelMapper.map(p, ProductMasterResponse.class))
                .collect(Collectors.toList());
    }

    public Map<String, Integer> getProductStockBreakdown(Long productId) {
        List<WarehouseStock> stocks = whStockRepository.findByProductId(productId);
        Map<String, Integer> breakdown = new HashMap<>();
        for (WarehouseStock s : stocks) {
            breakdown.put(s.getWarehouse().getName(), s.getQuantity());
        }
        return breakdown;
    }

    public List<ProductMasterResponse> getLowStockAlerts() {
        return productRepository.findLowStockProducts().stream()
                .map(p -> modelMapper.map(p, ProductMasterResponse.class))
                .collect(Collectors.toList());
    }
}
