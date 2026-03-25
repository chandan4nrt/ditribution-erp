package com.ecomm.nrt.master.controller;

import com.ecomm.nrt.master.service.MasterDataExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping("/api/master/export")
@RequiredArgsConstructor
@Tag(name = "Master Data Export", description = "Excel and PDF exports for Master Data")
public class MasterDataExportController {

    private final MasterDataExportService exportService;

    @GetMapping("/products/excel")
    @Operation(summary = "Export Product Master to Excel")
    public void exportProductsExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=products_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);

        exportService.exportProductsToExcel(response);
    }

    @GetMapping("/products/pdf")
    @Operation(summary = "Export Product Master to PDF")
    public void exportProductsPDF(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=products_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);

        exportService.exportProductsToPDF(response);
    }
}
