package com.ecomm.nrt.service;

import com.ecomm.nrt.dto.request.SalesInvoiceRequest;
import com.ecomm.nrt.dto.response.SalesInvoiceResponse;
import com.ecomm.nrt.entity.*;
import com.ecomm.nrt.repository.SalesInvoiceRepository;
import com.ecomm.nrt.repository.SalesOrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalesInvoiceService {

    private final SalesInvoiceRepository invoiceRepository;
    private final SalesOrderRepository soRepository;
    private final ModelMapper modelMapper;

    @Transactional
    @SuppressWarnings("null")
    public SalesInvoiceResponse createInvoice(SalesInvoiceRequest request) {
        SalesOrder so = soRepository.findById(request.getSalesOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Sales Order not found"));

        if (so.getStatus() == SalesStatus.DRAFT || so.getStatus() == SalesStatus.CANCELLED || so.getStatus() == SalesStatus.INVOICED) {
            throw new IllegalStateException("Cannot invoice SO in " + so.getStatus() + " status");
        }

        SalesInvoice invoice = SalesInvoice.builder()
                .invoiceNumber(request.getInvoiceNumber())
                .salesOrder(so)
                .invoiceDate(request.getInvoiceDate())
                .subTotal(so.getSubTotal())
                .taxAmount(so.getTaxAmount())
                .totalAmount(so.getTotalAmount())
                .remarks(request.getRemarks())
                .build();

        for (SalesOrderItem soItem : so.getItems()) {
            SalesInvoiceItem invItem = SalesInvoiceItem.builder()
                    .product(soItem.getProduct())
                    .quantity(soItem.getQuantity())
                    .unitPrice(soItem.getUnitPrice())
                    .lineTotal(soItem.getLineTotal())
                    .taxRate(soItem.getTaxRate())
                    .taxAmount(soItem.getTaxAmount())
                    .build();
            invoice.addItem(invItem);
        }

        SalesInvoice saved = invoiceRepository.save(invoice);
        
        // Update SO Status
        so.setStatus(SalesStatus.INVOICED);
        soRepository.save(so);

        return convertToResponse(saved);
    }

    private SalesInvoiceResponse convertToResponse(SalesInvoice invoice) {
        SalesInvoiceResponse res = modelMapper.map(invoice, SalesInvoiceResponse.class);
        res.setSoNumber(invoice.getSalesOrder().getSoNumber());
        res.setCustomerName(invoice.getSalesOrder().getCustomer().getName());
        res.setItems(invoice.getItems().stream().map(item -> {
            SalesInvoiceResponse.InvoiceItemResponse itemRes = modelMapper.map(item, SalesInvoiceResponse.InvoiceItemResponse.class);
            itemRes.setProductName(item.getProduct().getName());
            return itemRes;
        }).collect(Collectors.toList()));
        return res;
    }
}
