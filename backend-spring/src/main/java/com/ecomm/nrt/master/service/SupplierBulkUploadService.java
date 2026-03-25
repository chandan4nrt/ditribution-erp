package com.ecomm.nrt.master.service;

import com.ecomm.nrt.dto.response.BulkUploadResponse;
import com.ecomm.nrt.master.entity.Supplier;
import com.ecomm.nrt.master.repository.SupplierRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierBulkUploadService {

    private final SupplierRepository supplierRepository;

    @Transactional
    public BulkUploadResponse uploadSuppliers(MultipartFile file) {
        BulkUploadResponse response = new BulkUploadResponse();
        List<Supplier> suppliersToSave = new ArrayList<>();
        int totalRows = 0;

        try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(file.getInputStream()))
                .withSkipLines(1) // Skip header
                .build()) {

            String[] line;
            int rowNum = 1;
            while ((line = reader.readNext()) != null) {
                rowNum++;
                totalRows++;
                try {
                    if (line.length < 2) continue;

                    Supplier supplier = parseSupplierFromCSV(line);
                    if (supplierRepository.existsByCode(supplier.getCode())) {
                        response.getErrors().add("Row " + rowNum + ": Supplier code already exists: " + supplier.getCode());
                        response.setFailureCount(response.getFailureCount() + 1);
                        continue;
                    }

                    suppliersToSave.add(supplier);
                    response.setSuccessCount(response.getSuccessCount() + 1);
                } catch (Exception e) {
                    log.error("Error parsing supplier row {}: {}", rowNum, e.getMessage());
                    response.getErrors().add("Row " + rowNum + ": " + e.getMessage());
                    response.setFailureCount(response.getFailureCount() + 1);
                }
            }
            supplierRepository.saveAll(suppliersToSave);
            response.setTotalRecords(totalRows);

        } catch (Exception e) {
            log.error("Supplier bulk upload failed: {}", e.getMessage());
            response.getErrors().add("System error: " + e.getMessage());
        }

        return response;
    }

    private Supplier parseSupplierFromCSV(String[] line) {
        // Expected CSV format: code, name, contactPerson, email, phone, address, city, state, country, gstNo, paymentTerms, bankName, accountNo, ifsc
        return Supplier.builder()
                .code(getValue(line, 0))
                .name(getValue(line, 1))
                .contactPerson(getValue(line, 2))
                .email(getValue(line, 3))
                .phone(getValue(line, 4))
                .address(getValue(line, 5))
                .city(getValue(line, 6))
                .state(getValue(line, 7))
                .country(getValue(line, 8))
                .gstNo(getValue(line, 9))
                .paymentTerms(getValue(line, 10))
                .bankName(getValue(line, 11))
                .bankAccountNo(getValue(line, 12))
                .ifscCode(getValue(line, 13))
                .isActive(true)
                .build();
    }

    private String getValue(String[] line, int index) {
        if (index >= line.length || line[index] == null) return "";
        return line[index].trim();
    }
}
