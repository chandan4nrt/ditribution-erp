package com.ecomm.nrt.master.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WarehouseResponse {
    private Long id;
    private String code;
    private String name;
    private String location;
    private String city;
    private String state;
    private Long managerId;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
