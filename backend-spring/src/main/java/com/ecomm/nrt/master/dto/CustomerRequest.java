package com.ecomm.nrt.master.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CustomerRequest {
    @NotBlank @Size(max = 20)
    private String code;
    @NotBlank
    private String name;
    private String contactPerson;
    @Email private String email;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String country;
    @Size(max = 20) private String gstNo;
    private String paymentTerms;
    private BigDecimal creditLimit;
}
