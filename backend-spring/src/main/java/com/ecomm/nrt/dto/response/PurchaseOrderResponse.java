package com.ecomm.nrt.dto.response;

import com.ecomm.nrt.entity.PurchaseStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderResponse {
    private Long id;
    private String poNumber;
    private Long supplierId;
    private String supplierName;
    private Long warehouseId;
    private String warehouseName;
    private LocalDate poDate;
    private LocalDate expectedDeliveryDate;
    private PurchaseStatus status;
    private BigDecimal subTotal;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private String remarks;
    private List<POItemResponse> items;
    private LocalDateTime createdAt;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class POItemResponse {
        private Long id;
        private Long productId;
        private String productName;
        private String productCode;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal lineTotal;
        private BigDecimal taxRate;
        private BigDecimal taxAmount;
        private String remarks;
    }
}
