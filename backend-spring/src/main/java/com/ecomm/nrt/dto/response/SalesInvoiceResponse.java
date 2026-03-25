package com.ecomm.nrt.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesInvoiceResponse {
    private Long id;
    private String invoiceNumber;
    private String soNumber;
    private String customerName;
    private LocalDate invoiceDate;
    private BigDecimal subTotal;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private String remarks;
    private List<InvoiceItemResponse> items;
    private LocalDateTime createdAt;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InvoiceItemResponse {
        private Long id;
        private String productName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal lineTotal;
        private BigDecimal taxRate;
        private BigDecimal taxAmount;
    }
}
