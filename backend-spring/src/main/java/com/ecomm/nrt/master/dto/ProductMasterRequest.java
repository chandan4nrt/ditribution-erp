package com.ecomm.nrt.master.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductMasterRequest {
    @NotBlank @Size(max = 30)
    private String code;
    @NotBlank
    private String name;
    private String description;
    private String category;
    @Size(max = 20) private String unit;
    @Size(max = 10) private String hsnCode;
    private BigDecimal gstRate;
    @NotNull
    private BigDecimal basePrice;
    private BigDecimal discountPrice;
    private Integer reorderLevel;
}
