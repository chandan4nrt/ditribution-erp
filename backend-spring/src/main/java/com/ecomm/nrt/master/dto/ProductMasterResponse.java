package com.ecomm.nrt.master.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductMasterResponse {
    private Long id;
    private String code;
    private String name;
    private String description;
    private String category;
    private String unit;
    private String hsnCode;
    private BigDecimal gstRate;
    private BigDecimal basePrice;
    private BigDecimal discountPrice;
    private Integer reorderLevel;
    private Integer currentStock;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
