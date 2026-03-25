package com.ecomm.nrt.master.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CustomerResponse {
    private Long id;
    private String code;
    private String name;
    private String contactPerson;
    private String email;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String country;
    private String gstNo;
    private String paymentTerms;
    private BigDecimal creditLimit;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
