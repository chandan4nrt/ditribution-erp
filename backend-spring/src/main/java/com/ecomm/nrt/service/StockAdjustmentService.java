package com.ecomm.nrt.service;

import com.ecomm.nrt.dto.request.StockAdjustmentRequest;
import com.ecomm.nrt.dto.response.StockAdjustmentResponse;
import com.ecomm.nrt.entity.StockAdjustment;
import com.ecomm.nrt.entity.StockAdjustmentItem;
import com.ecomm.nrt.entity.WarehouseStock;
import com.ecomm.nrt.master.entity.ProductMaster;
import com.ecomm.nrt.master.entity.Warehouse;
import com.ecomm.nrt.master.service.ProductMasterService;
import com.ecomm.nrt.master.service.WarehouseService;
import com.ecomm.nrt.repository.StockAdjustmentRepository;
import com.ecomm.nrt.repository.WarehouseStockRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockAdjustmentService {

    private final StockAdjustmentRepository adjustmentRepository;
    private final WarehouseStockRepository whStockRepository;
    private final WarehouseService warehouseService;
    private final ProductMasterService productService;
    private final ModelMapper modelMapper;

    @Transactional
    @SuppressWarnings("null")
    public StockAdjustmentResponse createAdjustment(StockAdjustmentRequest request) {
        if (adjustmentRepository.findByAdjustmentNumber(request.getAdjustmentNumber()).isPresent()) {
            throw new IllegalArgumentException("Adjustment Number already exists: " + request.getAdjustmentNumber());
        }

        Warehouse warehouse = modelMapper.map(warehouseService.getById(request.getWarehouseId()), Warehouse.class);

        StockAdjustment adjustment = StockAdjustment.builder()
                .adjustmentNumber(request.getAdjustmentNumber())
                .warehouse(warehouse)
                .adjustmentDate(request.getAdjustmentDate())
                .remarks(request.getRemarks())
                .build();

        for (StockAdjustmentRequest.AdjustmentItemRequest itemReq : request.getItems()) {
            ProductMaster product = productService.findOrThrow(itemReq.getProductId());
            
            // Fetch/Create Warehouse Stock record
            WarehouseStock whStock = whStockRepository
                    .findByProductIdAndWarehouseId(product.getId(), warehouse.getId())
                    .orElse(WarehouseStock.builder()
                            .product(product)
                            .warehouse(warehouse)
                            .quantity(0)
                            .build());

            // Validate if deducting
            if (itemReq.getChangeQuantity() < 0 && (whStock.getQuantity() + itemReq.getChangeQuantity() < 0)) {
                throw new IllegalArgumentException("Insufficient stock in " + warehouse.getName() + 
                    " for " + product.getName() + " to subtract " + Math.abs(itemReq.getChangeQuantity()));
            }

            // Update Warehouse Stock
            whStock.setQuantity(whStock.getQuantity() + itemReq.getChangeQuantity());
            whStockRepository.save(whStock);

            // Update Global Stock
            int newGlobalStock = product.getCurrentStock() + itemReq.getChangeQuantity();
            productService.updateProductStock(product.getId(), newGlobalStock);

            StockAdjustmentItem item = StockAdjustmentItem.builder()
                    .product(product)
                    .changeQuantity(itemReq.getChangeQuantity())
                    .reason(itemReq.getReason())
                    .build();
            adjustment.addItem(item);
        }

        return convertToResponse(adjustmentRepository.save(adjustment));
    }

    private StockAdjustmentResponse convertToResponse(StockAdjustment adjustment) {
        StockAdjustmentResponse res = modelMapper.map(adjustment, StockAdjustmentResponse.class);
        res.setWarehouseId(adjustment.getWarehouse().getId());
        res.setWarehouseName(adjustment.getWarehouse().getName());
        res.setItems(adjustment.getItems().stream().map(item -> {
            StockAdjustmentResponse.AdjustmentItemResponse itemRes = modelMapper.map(item, StockAdjustmentResponse.AdjustmentItemResponse.class);
            itemRes.setProductName(item.getProduct().getName());
            itemRes.setProductId(item.getProduct().getId());
            return itemRes;
        }).collect(Collectors.toList()));
        return res;
    }
}
