package com.ecomm.nrt.service;

import com.ecomm.nrt.dto.request.PurchaseOrderRequest;
import com.ecomm.nrt.dto.response.PurchaseOrderResponse;
import com.ecomm.nrt.entity.PurchaseOrder;
import com.ecomm.nrt.entity.PurchaseOrderItem;
import com.ecomm.nrt.entity.PurchaseStatus;
import com.ecomm.nrt.master.entity.ProductMaster;
import com.ecomm.nrt.master.entity.Supplier;
import com.ecomm.nrt.master.entity.Warehouse;
import com.ecomm.nrt.master.service.ProductMasterService;
import com.ecomm.nrt.master.service.SupplierService;
import com.ecomm.nrt.master.service.WarehouseService;
import com.ecomm.nrt.repository.PurchaseOrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PurchaseOrderService {

    private final PurchaseOrderRepository poRepository;
    private final SupplierService supplierService;
    private final WarehouseService warehouseService;
    private final ProductMasterService productService;
    private final ModelMapper modelMapper;

    @Transactional
    @SuppressWarnings("null")
    public PurchaseOrderResponse createPO(PurchaseOrderRequest request) {
        if (poRepository.findByPoNumber(request.getPoNumber()).isPresent()) {
            throw new IllegalArgumentException("Purchase Order number already exists: " + request.getPoNumber());
        }

        Supplier supplier = modelMapper.map(supplierService.getById(request.getSupplierId()), Supplier.class);
        Warehouse warehouse = modelMapper.map(warehouseService.getById(request.getWarehouseId()), Warehouse.class);

        PurchaseOrder po = PurchaseOrder.builder()
                .poNumber(request.getPoNumber())
                .supplier(supplier)
                .warehouse(warehouse)
                .poDate(request.getPoDate())
                .expectedDeliveryDate(request.getExpectedDeliveryDate())
                .remarks(request.getRemarks())
                .status(PurchaseStatus.DRAFT)
                .build();

        BigDecimal subTotal = BigDecimal.ZERO;
        BigDecimal taxTotal = BigDecimal.ZERO;

        for (PurchaseOrderRequest.POItemRequest itemReq : request.getItems()) {
            ProductMaster product = productService.findOrThrow(itemReq.getProductId());
            
            BigDecimal lineTotal = itemReq.getUnitPrice().multiply(new BigDecimal(itemReq.getQuantity()));
            BigDecimal taxRate = product.getGstRate() != null ? product.getGstRate() : BigDecimal.ZERO;
            BigDecimal taxAmount = lineTotal.multiply(taxRate).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

            PurchaseOrderItem item = PurchaseOrderItem.builder()
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .unitPrice(itemReq.getUnitPrice())
                    .lineTotal(lineTotal)
                    .taxRate(taxRate)
                    .taxAmount(taxAmount)
                    .remarks(itemReq.getRemarks())
                    .build();

            po.addItem(item);
            subTotal = subTotal.add(lineTotal);
            taxTotal = taxTotal.add(taxAmount);
        }

        po.setSubTotal(subTotal);
        po.setTaxAmount(taxTotal);
        po.setTotalAmount(subTotal.add(taxTotal));

        PurchaseOrder saved = poRepository.save(po);
        return convertToResponse(saved);
    }

    public List<PurchaseOrderResponse> getAll() {
        return poRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public PurchaseOrderResponse getById(Long id) {
        if (id == null) throw new IllegalArgumentException("ID must not be null");
        PurchaseOrder po = poRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Purchase Order not found with ID: " + id));
        return convertToResponse(po);
    }

    @Transactional
    public void deletePO(Long id) {
        if (id == null) throw new IllegalArgumentException("ID must not be null");
        PurchaseOrder po = poRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Purchase Order not found"));
        if (po.getStatus() != PurchaseStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT orders can be deleted");
        }
        poRepository.delete(po);
    }

    @Transactional
    @SuppressWarnings("null")
    public PurchaseOrderResponse updatePOStatus(Long id, PurchaseStatus newStatus) {
        PurchaseOrder po = poRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Purchase Order not found with ID: " + id));
        
        // Basic state machine rules
        if (newStatus == PurchaseStatus.OPEN && po.getStatus() != PurchaseStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT orders can be set to OPEN");
        }
        if (newStatus == PurchaseStatus.CANCELLED && (po.getStatus() == PurchaseStatus.RECEIVED || po.getStatus() == PurchaseStatus.PARTIALLY_RECEIVED)) {
            throw new IllegalStateException("Cannot cancel a received order");
        }

        po.setStatus(newStatus);
        PurchaseOrder saved = poRepository.save(po);
        return convertToResponse(saved);
    }

    @SuppressWarnings("null")
    private PurchaseOrderResponse convertToResponse(PurchaseOrder po) {
        PurchaseOrderResponse res = modelMapper.map(po, PurchaseOrderResponse.class);
        res.setSupplierId(po.getSupplier().getId());
        res.setSupplierName(po.getSupplier().getName());
        res.setWarehouseId(po.getWarehouse().getId());
        res.setWarehouseName(po.getWarehouse().getName());
        
        res.setItems(po.getItems().stream().map(item -> {
            PurchaseOrderResponse.POItemResponse itemRes = modelMapper.map(item, PurchaseOrderResponse.POItemResponse.class);
            itemRes.setProductId(item.getProduct().getId());
            itemRes.setProductName(item.getProduct().getName());
            itemRes.setProductCode(item.getProduct().getCode());
            return itemRes;
        }).collect(Collectors.toList()));
        
        return res;
    }
}
