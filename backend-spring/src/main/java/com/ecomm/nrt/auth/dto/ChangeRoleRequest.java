package com.ecomm.nrt.auth.dto;

import com.ecomm.nrt.auth.entity.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeRoleRequest {

    @NotNull(message = "Role is required")
    private Role role;
}
