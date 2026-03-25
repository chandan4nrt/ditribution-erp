package com.ecomm.nrt.auth.controller;

import com.ecomm.nrt.auth.dto.AuthResponse;
import com.ecomm.nrt.auth.dto.LoginRequest;
import com.ecomm.nrt.auth.dto.RegisterRequest;
import com.ecomm.nrt.auth.dto.UserProfileResponse;
import com.ecomm.nrt.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register, login, and user management endpoints")
public class AuthController {

    private final AuthService authService;

    // ── Register (ADMIN creates new users) ─────────────────────────────────
    @PostMapping("/register")
    @Operation(summary = "Register a new user (Admin only)")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    // ── Login ──────────────────────────────────────────────────────────────
    @PostMapping("/login")
    @Operation(summary = "Login and receive JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // ── Get own profile ────────────────────────────────────────────────────
    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user's profile",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserProfileResponse> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(authService.getProfile(userDetails.getUsername()));
    }

    // ── List all users (Admin only) ────────────────────────────────────────
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users (Admin only)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        return ResponseEntity.ok(authService.getAllUsers());
    }

    // ── Toggle user active/inactive (Admin only) ───────────────────────────
    @PatchMapping("/users/{userId}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activate or deactivate a user (Admin only)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserProfileResponse> toggleStatus(@PathVariable Long userId) {
        return ResponseEntity.ok(authService.toggleUserStatus(userId));
    }
}
