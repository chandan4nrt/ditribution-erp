package com.ecomm.nrt.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GRNResponse {
    private Long id;
    private String grnNumber;
    private String poNumber;
    private LocalDate receivedDate;
    private String remarks;
    private List<GRNItemResponse> items;
    private LocalDateTime createdAt;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GRNItemResponse {
        private Long id;
        private Long productId;
        private String productName;
        private String productCode;
        private Integer receivedQuantity;
        private String remarks;
    }
}
