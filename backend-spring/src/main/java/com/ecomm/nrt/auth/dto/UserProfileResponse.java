package com.ecomm.nrt.auth.dto;

import com.ecomm.nrt.auth.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private Role role;
    private boolean isActive;
    private boolean isApproved;
    private LocalDateTime createdAt;
}
