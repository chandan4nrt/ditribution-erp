package com.ecomm.nrt.master.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class WarehouseRequest {
    @NotBlank @Size(max = 20)
    private String code;
    @NotBlank
    private String name;
    private String location;
    private String city;
    private String state;
    private Long managerId;
}
