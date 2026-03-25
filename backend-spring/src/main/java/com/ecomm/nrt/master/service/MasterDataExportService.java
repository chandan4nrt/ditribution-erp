package com.ecomm.nrt.master.service;

import com.ecomm.nrt.master.entity.ProductMaster;
import com.ecomm.nrt.master.repository.ProductMasterRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MasterDataExportService {

    private final ProductMasterRepository productRepository;

    public void exportProductsToExcel(HttpServletResponse response) throws IOException {
        List<ProductMaster> products = productRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Products");

            // Header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Code", "Name", "Category", "Unit", "HSN", "GST %", "Base Price", "Current Stock"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                
                CellStyle style = workbook.createCellStyle();
                org.apache.poi.ss.usermodel.Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }

            int rowIdx = 1;
            for (ProductMaster p : products) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(p.getCode());
                row.createCell(1).setCellValue(p.getName());
                row.createCell(2).setCellValue(p.getCategory());
                row.createCell(3).setCellValue(p.getUnit());
                row.createCell(4).setCellValue(p.getHsnCode());
                row.createCell(5).setCellValue(p.getGstRate() != null ? p.getGstRate().doubleValue() : 0.0);
                row.createCell(6).setCellValue(p.getBasePrice() != null ? p.getBasePrice().doubleValue() : 0.0);
                row.createCell(7).setCellValue(p.getCurrentStock());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(response.getOutputStream());
        }
    }

    public void exportProductsToPDF(HttpServletResponse response) throws IOException {
        List<ProductMaster> products = productRepository.findAll();

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        
        Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitle.setSize(18);
        Paragraph title = new Paragraph("Product Master List", fontTitle);
        title.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100f);
        table.setWidths(new float[] {2f, 4f, 2f, 2f, 2f, 2f});
        table.setSpacingBefore(10);

        writeTableHeader(table);
        writeTableData(table, products);

        document.add(table);
        document.close();
    }

    private void writeTableHeader(PdfPTable table) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        cell.setPadding(5);
        
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setColor(Color.WHITE);

        String[] headers = {"Code", "Name", "Category", "Unit", "Base Price", "Stock"};
        for (String h : headers) {
            cell.setPhrase(new Phrase(h, font));
            table.addCell(cell);
        }
    }

    private void writeTableData(PdfPTable table, List<ProductMaster> products) {
        for (ProductMaster p : products) {
            table.addCell(p.getCode());
            table.addCell(p.getName());
            table.addCell(p.getCategory());
            table.addCell(p.getUnit());
            table.addCell(String.valueOf(p.getBasePrice()));
            table.addCell(String.valueOf(p.getCurrentStock()));
        }
    }
}
