package com.ecomm.nrt.dto.response;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkUploadResponse {
    private int totalRecords;
    private int successCount;
    private int failureCount;
    @Builder.Default
    private List<String> errors = new ArrayList<>();
}
