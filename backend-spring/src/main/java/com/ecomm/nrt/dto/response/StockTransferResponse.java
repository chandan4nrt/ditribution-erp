package com.ecomm.nrt.dto.response;

import com.ecomm.nrt.entity.TransferStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockTransferResponse {
    private Long id;
    private String transferNumber;
    private Long fromWarehouseId;
    private String fromWarehouseName;
    private Long toWarehouseId;
    private String toWarehouseName;
    private LocalDate transferDate;
    private TransferStatus status;
    private String remarks;
    private List<TransferItemResponse> items;
    private LocalDateTime createdAt;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TransferItemResponse {
        private Long id;
        private Long productId;
        private String productName;
        private String productCode;
        private Integer quantity;
        private String remarks;
    }
}
