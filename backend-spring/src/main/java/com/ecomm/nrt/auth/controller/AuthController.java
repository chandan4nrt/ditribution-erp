package com.ecomm.nrt.auth.controller;

import com.ecomm.nrt.auth.dto.*;
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

    // ── Public Signup (waitlist) ───────────────────────────────────────────
    @PostMapping("/signup")
    @Operation(summary = "Public signup - account goes to admin approval waitlist")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserProfileResponse> signup(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.signup(request));
    }

    // ── Admin: Register & immediately activate a user ─────────────────────
    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Admin-only: Create and immediately activate a user",
               security = @SecurityRequirement(name = "bearerAuth"))
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

    // ── List pending / waitlist users (Admin only) ─────────────────────────
    @GetMapping("/users/pending")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users pending approval (Admin only)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<UserProfileResponse>> getPendingUsers() {
        return ResponseEntity.ok(authService.getPendingUsers());
    }

    // ── Approve a user (Admin only) ────────────────────────────────────────
    @PostMapping("/users/{userId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve a pending user (Admin only)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserProfileResponse> approveUser(@PathVariable Long userId) {
        return ResponseEntity.ok(authService.approveUser(userId));
    }

    // ── Toggle user active/blocked (Admin only) ────────────────────────────
    @PatchMapping("/users/{userId}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Block or unblock a user (Admin only)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserProfileResponse> toggleStatus(@PathVariable Long userId) {
        return ResponseEntity.ok(authService.toggleUserStatus(userId));
    }

    // ── Change user role (Admin only) ──────────────────────────────────────
    @PatchMapping("/users/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Change a user's role (Admin only)",
               security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<UserProfileResponse> changeRole(
            @PathVariable Long userId,
            @Valid @RequestBody ChangeRoleRequest request) {
        return ResponseEntity.ok(authService.changeUserRole(userId, request.getRole()));
    }
}
