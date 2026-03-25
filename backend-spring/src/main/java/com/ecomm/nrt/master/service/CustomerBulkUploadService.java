package com.ecomm.nrt.master.service;

import com.ecomm.nrt.dto.response.BulkUploadResponse;
import com.ecomm.nrt.master.entity.Customer;
import com.ecomm.nrt.master.repository.CustomerRepository;
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
public class CustomerBulkUploadService {

    private final CustomerRepository customerRepository;

    @Transactional
    public BulkUploadResponse uploadCustomers(MultipartFile file) {
        BulkUploadResponse response = new BulkUploadResponse();
        List<Customer> customersToSave = new ArrayList<>();
        int totalRows = 0;

        try (CSVReader reader = new CSVReaderBuilder(new InputStreamReader(file.getInputStream()))
                .withSkipLines(1)
                .build()) {

            String[] line;
            int rowNum = 1;
            while ((line = reader.readNext()) != null) {
                rowNum++;
                totalRows++;
                try {
                    if (line.length < 2) continue;

                    Customer customer = parseCustomerFromCSV(line);
                    if (customerRepository.existsByCode(customer.getCode())) {
                        response.getErrors().add("Row " + rowNum + ": Customer code already exists: " + customer.getCode());
                        response.setFailureCount(response.getFailureCount() + 1);
                        continue;
                    }

                    customersToSave.add(customer);
                    response.setSuccessCount(response.getSuccessCount() + 1);
                } catch (Exception e) {
                    log.error("Error parsing customer row {}: {}", rowNum, e.getMessage());
                    response.getErrors().add("Row " + rowNum + ": " + e.getMessage());
                    response.setFailureCount(response.getFailureCount() + 1);
                }
            }
            customerRepository.saveAll(customersToSave);
            response.setTotalRecords(totalRows);

        } catch (Exception e) {
            log.error("Customer bulk upload failed: {}", e.getMessage());
            response.getErrors().add("System error: " + e.getMessage());
        }

        return response;
    }

    private Customer parseCustomerFromCSV(String[] line) {
        // Expected: code, name, email, phone, address, city, state, country, gstNo, creditLimit, creditDays
        return Customer.builder()
                .code(getValue(line, 0))
                .name(getValue(line, 1))
                .email(getValue(line, 2))
                .phone(getValue(line, 3))
                .address(getValue(line, 4))
                .city(getValue(line, 5))
                .state(getValue(line, 6))
                .country(getValue(line, 7))
                .gstNo(getValue(line, 8))
                .isActive(true)
                .build();
    }

    private String getValue(String[] line, int index) {
        if (index >= line.length || line[index] == null) return "";
        return line[index].trim();
    }
}
