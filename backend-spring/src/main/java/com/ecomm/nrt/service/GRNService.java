package com.ecomm.nrt.service;

import com.ecomm.nrt.dto.request.GRNRequest;
import com.ecomm.nrt.dto.response.GRNResponse;
import com.ecomm.nrt.entity.*;
import com.ecomm.nrt.master.entity.ProductMaster;
import com.ecomm.nrt.master.repository.ProductMasterRepository;
import com.ecomm.nrt.repository.GoodsReceivedNoteRepository;
import com.ecomm.nrt.repository.PurchaseOrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GRNService {

    private final GoodsReceivedNoteRepository grnRepository;
    private final PurchaseOrderRepository poRepository;
    private final ProductMasterRepository productRepository;
    private final com.ecomm.nrt.repository.WarehouseStockRepository warehouseStockRepository;
    private final ModelMapper modelMapper;

    @Transactional
    @SuppressWarnings("null")
    public GRNResponse createGRN(GRNRequest request) {
        PurchaseOrder po = poRepository.findById(request.getPurchaseOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Purchase Order not found"));

        if (po.getStatus() == PurchaseStatus.DRAFT || po.getStatus() == PurchaseStatus.CANCELLED || po.getStatus() == PurchaseStatus.RECEIVED) {
            throw new IllegalStateException("Cannot receive Goods for PO in " + po.getStatus() + " status");
        }

        GoodsReceivedNote grn = GoodsReceivedNote.builder()
                .grnNumber(request.getGrnNumber())
                .purchaseOrder(po)
                .receivedDate(request.getReceivedDate())
                .remarks(request.getRemarks())
                .build();

        // Map PO Items for easy validation
        Map<Long, Integer> orderedQuantities = po.getItems().stream()
                .collect(Collectors.toMap(item -> item.getProduct().getId(), PurchaseOrderItem::getQuantity));

        // Get already received quantities across all GRNs for this PO
        List<GoodsReceivedNote> existingGRNs = grnRepository.findByPurchaseOrderId(po.getId());
        Map<Long, Integer> receivedSoFar = existingGRNs.stream()
                .flatMap(g -> g.getItems().stream())
                .collect(Collectors.groupingBy(item -> item.getProduct().getId(),
                        Collectors.summingInt(GRNItem::getReceivedQuantity)));

        for (GRNRequest.GRNItemRequest itemReq : request.getItems()) {
            Long productId = itemReq.getProductId();
            if (!orderedQuantities.containsKey(productId)) {
                throw new IllegalArgumentException("Product ID " + productId + " is not part of this Purchase Order");
            }

            int orderedQty = orderedQuantities.get(productId);
            int soFar = receivedSoFar.getOrDefault(productId, 0);
            int current = itemReq.getQuantity();

            if (soFar + current > orderedQty) {
                throw new IllegalArgumentException("Total received quantity (" + (soFar + current) + 
                    ") exceeds ordered quantity (" + orderedQty + ") for product ID " + productId);
            }

            ProductMaster product = productRepository.findById(productId)
                    .orElseThrow(() -> new EntityNotFoundException("Product not found: " + productId));

            GRNItem grnItem = GRNItem.builder()
                    .product(product)
                    .receivedQuantity(current)
                    .remarks(itemReq.getRemarks())
                    .build();

            grn.addItem(grnItem);

            // Update Global Stock in Product Master
            product.setCurrentStock(product.getCurrentStock() + current);
            productRepository.save(product);

            // Update Warehouse-Specific Stock
            com.ecomm.nrt.entity.WarehouseStock whStock = warehouseStockRepository
                .findByProductIdAndWarehouseId(productId, po.getWarehouse().getId())
                .orElse(com.ecomm.nrt.entity.WarehouseStock.builder()
                        .product(product)
                        .warehouse(po.getWarehouse())
                        .quantity(0)
                        .build());
            
            whStock.setQuantity(whStock.getQuantity() + current);
            warehouseStockRepository.save(whStock);
        }

        GoodsReceivedNote saved = grnRepository.save(grn);

        // Update PO Status
        updatePOStatusAfterGRN(po);

        return convertToResponse(saved);
    }

    private void updatePOStatusAfterGRN(PurchaseOrder po) {
        Map<Long, Integer> ordered = po.getItems().stream()
                .collect(Collectors.toMap(item -> item.getProduct().getId(), PurchaseOrderItem::getQuantity));

        List<GoodsReceivedNote> allGrns = grnRepository.findByPurchaseOrderId(po.getId());
        Map<Long, Integer> totalReceived = allGrns.stream()
                .flatMap(g -> g.getItems().stream())
                .collect(Collectors.groupingBy(item -> item.getProduct().getId(),
                        Collectors.summingInt(GRNItem::getReceivedQuantity)));

        boolean allReceived = true;
        boolean anyReceived = false;

        for (Map.Entry<Long, Integer> entry : ordered.entrySet()) {
            int received = totalReceived.getOrDefault(entry.getKey(), 0);
            if (received < entry.getValue()) {
                allReceived = false;
            }
            if (received > 0) {
                anyReceived = true;
            }
        }

        if (allReceived) {
            po.setStatus(PurchaseStatus.RECEIVED);
        } else if (anyReceived) {
            po.setStatus(PurchaseStatus.PARTIALLY_RECEIVED);
        }
        
        poRepository.save(po);
    }

    private GRNResponse convertToResponse(GoodsReceivedNote grn) {
        GRNResponse res = modelMapper.map(grn, GRNResponse.class);
        res.setPoNumber(grn.getPurchaseOrder().getPoNumber());
        res.setItems(grn.getItems().stream().map(item -> {
            GRNResponse.GRNItemResponse itemRes = modelMapper.map(item, GRNResponse.GRNItemResponse.class);
            itemRes.setProductId(item.getProduct().getId());
            itemRes.setProductName(item.getProduct().getName());
            itemRes.setProductCode(item.getProduct().getCode());
            return itemRes;
        }).collect(Collectors.toList()));
        return res;
    }
}
