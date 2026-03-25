package com.ecomm.nrt.service;

import com.ecomm.nrt.dto.request.StockTransferRequest;
import com.ecomm.nrt.dto.response.StockTransferResponse;
import com.ecomm.nrt.entity.StockTransfer;
import com.ecomm.nrt.entity.StockTransferItem;
import com.ecomm.nrt.entity.TransferStatus;
import com.ecomm.nrt.entity.WarehouseStock;
import com.ecomm.nrt.master.entity.ProductMaster;
import com.ecomm.nrt.master.entity.Warehouse;
import com.ecomm.nrt.master.service.ProductMasterService;
import com.ecomm.nrt.master.service.WarehouseService;
import com.ecomm.nrt.repository.StockTransferRepository;
import com.ecomm.nrt.repository.WarehouseStockRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockTransferService {

    private final StockTransferRepository stRepository;
    private final WarehouseStockRepository whStockRepository;
    private final WarehouseService warehouseService;
    private final ProductMasterService productService;
    private final ModelMapper modelMapper;

    @Transactional
    @SuppressWarnings("null")
    public StockTransferResponse createTransfer(StockTransferRequest request) {
        if (stRepository.findByTransferNumber(request.getTransferNumber()).isPresent()) {
            throw new IllegalArgumentException("Transfer Number already exists: " + request.getTransferNumber());
        }

        Warehouse fromWh = modelMapper.map(warehouseService.getById(request.getFromWarehouseId()), Warehouse.class);
        Warehouse toWh = modelMapper.map(warehouseService.getById(request.getToWarehouseId()), Warehouse.class);

        StockTransfer transfer = StockTransfer.builder()
                .transferNumber(request.getTransferNumber())
                .fromWarehouse(fromWh)
                .toWarehouse(toWh)
                .transferDate(request.getTransferDate())
                .status(TransferStatus.DRAFT)
                .remarks(request.getRemarks())
                .build();

        for (StockTransferRequest.TransferItemRequest itemReq : request.getItems()) {
            ProductMaster product = productService.findOrThrow(itemReq.getProductId());
            
            // Check availability in source warehouse
            int available = whStockRepository.findByProductIdAndWarehouseId(product.getId(), fromWh.getId())
                    .map(WarehouseStock::getQuantity)
                    .orElse(0);

            if (available < itemReq.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock in " + fromWh.getName() + 
                    " for " + product.getName() + " (Available: " + available + ")");
            }

            StockTransferItem item = StockTransferItem.builder()
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .remarks(itemReq.getRemarks())
                    .build();
            transfer.addItem(item);
        }

        return convertToResponse(stRepository.save(transfer));
    }

    @Transactional
    @SuppressWarnings("null")
    public StockTransferResponse updateStatus(Long id, TransferStatus newStatus) {
        StockTransfer transfer = stRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transfer not found"));

        if (newStatus == TransferStatus.SENT && transfer.getStatus() == TransferStatus.DRAFT) {
            processStockMove(transfer, true); // Deduct from source
        } else if (newStatus == TransferStatus.RECEIVED && transfer.getStatus() == TransferStatus.SENT) {
            processStockMove(transfer, false); // Add to destination
        } else if (newStatus == TransferStatus.RECEIVED && transfer.getStatus() == TransferStatus.DRAFT) {
            // Direct move
            processStockMove(transfer, true);
            processStockMove(transfer, false);
        }

        transfer.setStatus(newStatus);
        return convertToResponse(stRepository.save(transfer));
    }

    private void processStockMove(StockTransfer transfer, boolean isDeduction) {
        Warehouse warehouse = isDeduction ? transfer.getFromWarehouse() : transfer.getToWarehouse();
        
        for (StockTransferItem item : transfer.getItems()) {
            WarehouseStock whStock = whStockRepository
                    .findByProductIdAndWarehouseId(item.getProduct().getId(), warehouse.getId())
                    .orElse(WarehouseStock.builder()
                            .product(item.getProduct())
                            .warehouse(warehouse)
                            .quantity(0)
                            .build());

            if (isDeduction) {
                if (whStock.getQuantity() < item.getQuantity()) {
                    throw new IllegalStateException("Insufficient stock in " + warehouse.getName() + " for " + item.getProduct().getName());
                }
                whStock.setQuantity(whStock.getQuantity() - item.getQuantity());
            } else {
                whStock.setQuantity(whStock.getQuantity() + item.getQuantity());
            }
            whStockRepository.save(whStock);
        }
    }

    private StockTransferResponse convertToResponse(StockTransfer transfer) {
        StockTransferResponse res = modelMapper.map(transfer, StockTransferResponse.class);
        res.setFromWarehouseId(transfer.getFromWarehouse().getId());
        res.setFromWarehouseName(transfer.getFromWarehouse().getName());
        res.setToWarehouseId(transfer.getToWarehouse().getId());
        res.setToWarehouseName(transfer.getToWarehouse().getName());
        
        res.setItems(transfer.getItems().stream().map(item -> {
            StockTransferResponse.TransferItemResponse itemRes = modelMapper.map(item, StockTransferResponse.TransferItemResponse.class);
            itemRes.setProductId(item.getProduct().getId());
            itemRes.setProductName(item.getProduct().getName());
            itemRes.setProductCode(item.getProduct().getCode());
            return itemRes;
        }).collect(Collectors.toList()));
        return res;
    }
}
