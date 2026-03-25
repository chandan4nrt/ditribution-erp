package com.ecomm.nrt.service;

import com.ecomm.nrt.dto.request.SalesOrderRequest;
import com.ecomm.nrt.dto.response.SalesOrderResponse;
import com.ecomm.nrt.entity.SalesOrder;
import com.ecomm.nrt.entity.SalesOrderItem;
import com.ecomm.nrt.entity.SalesStatus;
import com.ecomm.nrt.master.entity.Customer;
import com.ecomm.nrt.master.entity.ProductMaster;
import com.ecomm.nrt.master.entity.Warehouse;
import com.ecomm.nrt.master.service.CustomerService;
import com.ecomm.nrt.master.service.ProductMasterService;
import com.ecomm.nrt.master.service.WarehouseService;
import com.ecomm.nrt.repository.SalesOrderRepository;
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
public class SalesOrderService {

    private final SalesOrderRepository soRepository;
    private final CustomerService customerService;
    private final WarehouseService warehouseService;
    private final ProductMasterService productService;
    private final com.ecomm.nrt.repository.WarehouseStockRepository warehouseStockRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public SalesOrderResponse createSO(SalesOrderRequest request) {
        if (soRepository.findBySoNumber(request.getSoNumber()).isPresent()) {
            throw new IllegalArgumentException("Sales Order number already exists: " + request.getSoNumber());
        }

        Customer customer = modelMapper.map(customerService.getById(request.getCustomerId()), Customer.class);
        Warehouse warehouse = modelMapper.map(warehouseService.getById(request.getWarehouseId()), Warehouse.class);

        SalesOrder so = SalesOrder.builder()
                .soNumber(request.getSoNumber())
                .customer(customer)
                .warehouse(warehouse)
                .soDate(request.getSoDate())
                .remarks(request.getRemarks())
                .status(SalesStatus.DRAFT)
                .build();

        BigDecimal subTotal = BigDecimal.ZERO;
        BigDecimal taxTotal = BigDecimal.ZERO;

        for (SalesOrderRequest.SOItemRequest itemReq : request.getItems()) {
            ProductMaster product = productService.findOrThrow(itemReq.getProductId());
            
            // Availability check for specific warehouse
            int available = warehouseStockRepository.findByProductIdAndWarehouseId(product.getId(), warehouse.getId())
                    .map(com.ecomm.nrt.entity.WarehouseStock::getQuantity)
                    .orElse(0);

            if (available < itemReq.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock in warehouse " + warehouse.getName() + 
                    " for product " + product.getName() + " (Available: " + available + ", Requested: " + itemReq.getQuantity() + ")");
            }

            BigDecimal lineTotal = itemReq.getUnitPrice().multiply(new BigDecimal(itemReq.getQuantity()));
            BigDecimal taxRate = product.getGstRate() != null ? product.getGstRate() : BigDecimal.ZERO;
            BigDecimal taxAmount = lineTotal.multiply(taxRate).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

            SalesOrderItem item = SalesOrderItem.builder()
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .unitPrice(itemReq.getUnitPrice())
                    .lineTotal(lineTotal)
                    .taxRate(taxRate)
                    .taxAmount(taxAmount)
                    .remarks(itemReq.getRemarks())
                    .build();

            so.addItem(item);
            subTotal = subTotal.add(lineTotal);
            taxTotal = taxTotal.add(taxAmount);
        }

        so.setSubTotal(subTotal);
        so.setTaxAmount(taxTotal);
        so.setTotalAmount(subTotal.add(taxTotal));

        SalesOrder saved = soRepository.save(so);
        return convertToResponse(saved);
    }

    public List<SalesOrderResponse> getAll() {
        return soRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("null")
    public SalesOrderResponse getById(Long id) {
        SalesOrder so = soRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sales Order not found with ID: " + id));
        return convertToResponse(so);
    }

    @Transactional
    @SuppressWarnings("null")
    public SalesOrderResponse updateStatus(Long id, SalesStatus newStatus) {
        SalesOrder so = soRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sales Order not found"));
        
        // Finalize SO and deduct stock when moving out of DRAFT
        if (newStatus == SalesStatus.OPEN && so.getStatus() == SalesStatus.DRAFT) {
            deductStock(so);
        }

        so.setStatus(newStatus);
        return convertToResponse(soRepository.save(so));
    }

    private void deductStock(SalesOrder so) {
        for (SalesOrderItem item : so.getItems()) {
            ProductMaster product = item.getProduct();
            Warehouse warehouse = so.getWarehouse();

            com.ecomm.nrt.entity.WarehouseStock whStock = warehouseStockRepository
                    .findByProductIdAndWarehouseId(product.getId(), warehouse.getId())
                    .orElseThrow(() -> new IllegalStateException("Stock record not found for product in " + warehouse.getName()));

            if (whStock.getQuantity() < item.getQuantity()) {
                throw new IllegalStateException("Stock became insufficient in " + warehouse.getName() + " for product: " + product.getName());
            }

            // Deduct from Warehouse Stock
            whStock.setQuantity(whStock.getQuantity() - item.getQuantity());
            warehouseStockRepository.save(whStock);

            // Deduct from Global Stock
            product.setCurrentStock(product.getCurrentStock() - item.getQuantity());
            productService.updateProductStock(product.getId(), product.getCurrentStock()); // Assuming this method exists or I'll add it
        }
    }

    @SuppressWarnings("null")
    private SalesOrderResponse convertToResponse(SalesOrder so) {
        SalesOrderResponse res = modelMapper.map(so, SalesOrderResponse.class);
        res.setCustomerId(so.getCustomer().getId());
        res.setCustomerName(so.getCustomer().getName());
        res.setWarehouseId(so.getWarehouse().getId());
        res.setWarehouseName(so.getWarehouse().getName());
        
        res.setItems(so.getItems().stream().map(item -> {
            SalesOrderResponse.SOItemResponse itemRes = modelMapper.map(item, SalesOrderResponse.SOItemResponse.class);
            itemRes.setProductId(item.getProduct().getId());
            itemRes.setProductName(item.getProduct().getName());
            itemRes.setProductCode(item.getProduct().getCode());
            return itemRes;
        }).collect(Collectors.toList()));
        
        return res;
    }
}
