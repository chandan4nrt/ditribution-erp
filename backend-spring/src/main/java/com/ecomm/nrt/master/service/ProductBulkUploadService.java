package com.ecomm.nrt.master.service;

import com.ecomm.nrt.dto.response.BulkUploadResponse;
import com.ecomm.nrt.master.entity.ProductMaster;
import com.ecomm.nrt.master.repository.ProductMasterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductBulkUploadService {

    private final ProductMasterRepository productMasterRepository;

    @Transactional
    public BulkUploadResponse uploadProductMaster(MultipartFile file) {
        BulkUploadResponse response = new BulkUploadResponse();
        List<ProductMaster> productsToSave = new ArrayList<>();
        int totalRows = 0;

        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            // Skip header
            if (rows.hasNext()) rows.next();

            int rowNum = 1;
            while (rows.hasNext()) {
                rowNum++;
                totalRows++;
                Row row = rows.next();
                try {
                    ProductMaster product = parseProductFromRow(row);
                    if (productMasterRepository.existsByCode(product.getCode())) {
                        response.getErrors().add("Row " + rowNum + ": Product code already exists: " + product.getCode());
                        response.setFailureCount(response.getFailureCount() + 1);
                        continue;
                    }
                    productsToSave.add(product);
                    response.setSuccessCount(response.getSuccessCount() + 1);
                } catch (Exception e) {
                    log.error("Error parsing row {}: {}", rowNum, e.getMessage());
                    response.getErrors().add("Row " + rowNum + ": " + e.getMessage());
                    response.setFailureCount(response.getFailureCount() + 1);
                }
            }
            productMasterRepository.saveAll(productsToSave);
            response.setTotalRecords(totalRows);

        } catch (Exception e) {
            log.error("Bulk upload failed: {}", e.getMessage());
            response.getErrors().add("System error: " + e.getMessage());
        }

        return response;
    }

    private ProductMaster parseProductFromRow(Row row) {
        return ProductMaster.builder()
                .code(getCellValue(row, 0))
                .name(getCellValue(row, 1))
                .description(getCellValue(row, 2))
                .category(getCellValue(row, 3))
                .unit(getCellValue(row, 4))
                .hsnCode(getCellValue(row, 5))
                .gstRate(new BigDecimal(getCellValue(row, 6, "0.0")))
                .basePrice(new BigDecimal(getCellValue(row, 7, "0.0")))
                .discountPrice(new BigDecimal(getCellValue(row, 8, "0.0")))
                .reorderLevel(Integer.parseInt(getCellValue(row, 9, "0")))
                .currentStock(0)
                .isActive(true)
                .build();
    }

    private String getCellValue(Row row, int colIndex) {
        return getCellValue(row, colIndex, "");
    }

    private String getCellValue(Row row, int colIndex, String defaultValue) {
        Cell cell = row.getCell(colIndex);
        if (cell == null) return defaultValue;
        
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue().toString();
                }
                yield String.valueOf(cell.getNumericCellValue());
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> defaultValue;
        };
    }
}
