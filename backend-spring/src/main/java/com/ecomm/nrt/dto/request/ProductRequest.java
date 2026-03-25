package com.ecomm.nrt.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {
    @NotBlank(message = "Product name cannot be empty")
    private String name;
    private String description;
    @Positive(message = "Base price should be positive")
    private Double basePrice;
    private Double discountPrice;
}
