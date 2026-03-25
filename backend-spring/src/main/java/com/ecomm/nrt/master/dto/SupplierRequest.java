package com.ecomm.nrt.master.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SupplierRequest {
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
    private String bankName;
    private String bankAccountNo;
    private String ifscCode;
}
