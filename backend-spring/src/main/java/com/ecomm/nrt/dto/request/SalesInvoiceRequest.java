package com.ecomm.nrt.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesInvoiceRequest {

    @NotBlank(message = "Invoice Number is required")
    private String invoiceNumber;

    @NotNull(message = "Sales Order ID is required")
    private Long salesOrderId;

    @NotNull(message = "Invoice Date is required")
    private LocalDate invoiceDate;

    private String remarks;
}
