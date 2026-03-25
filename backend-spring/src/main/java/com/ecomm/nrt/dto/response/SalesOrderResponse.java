package com.ecomm.nrt.dto.response;

import com.ecomm.nrt.entity.SalesStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesOrderResponse {
    private Long id;
    private String soNumber;
    private Long customerId;
    private String customerName;
    private Long warehouseId;
    private String warehouseName;
    private LocalDate soDate;
    private SalesStatus status;
    private BigDecimal subTotal;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private String remarks;
    private List<SOItemResponse> items;
    private LocalDateTime createdAt;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SOItemResponse {
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
