package com.ecomm.nrt.auth.service;

import com.ecomm.nrt.auth.dto.*;
import com.ecomm.nrt.auth.entity.Role;
import com.ecomm.nrt.auth.entity.User;
import com.ecomm.nrt.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // ── Public Signup (goes to waitlist, not active/approved yet) ──────────
    @SuppressWarnings("null")
    public UserProfileResponse signup(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already taken: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());
        }

        // Check if this is the first user in the system
        boolean isFirstUser = userRepository.count() == 0;

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .fullName(request.getFullName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(isFirstUser ? Role.ADMIN : request.getRole()) // First user MUST be ADMIN
                .isActive(true)
                .isApproved(isFirstUser) // Auto-approve only the first user
                .build();

        User savedUser = userRepository.save(user);
        return toProfileResponse(savedUser);
    }

    // ── Admin: Register & immediately approve a user (legacy admin flow) ───
    @SuppressWarnings("null")
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already taken: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .fullName(request.getFullName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .isActive(true)
                .isApproved(true)   // admin-created users are immediately approved
                .build();

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser);

        return buildAuthResponse(token, savedUser);
    }

    // ── Login ──────────────────────────────────────────────────────────────
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!user.isApproved()) {
            throw new DisabledException("Your account is pending admin approval.");
        }
        if (!user.isActive()) {
            throw new DisabledException("Your account has been blocked by an administrator.");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        String token = jwtService.generateToken(user);
        return buildAuthResponse(token, user);
    }

    // ── Get Profile ────────────────────────────────────────────────────────
    public UserProfileResponse getProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        return toProfileResponse(user);
    }

    // ── Get All Users (Admin) ──────────────────────────────────────────────
    public List<UserProfileResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toProfileResponse)
                .toList();
    }

    // ── Get Pending/Waitlist Users (Admin) ─────────────────────────────────
    public List<UserProfileResponse> getPendingUsers() {
        return userRepository.findByIsApproved(false).stream()
                .map(this::toProfileResponse)
                .toList();
    }

    // ── Approve user (Admin) ───────────────────────────────────────────────
    @SuppressWarnings("null")
    public UserProfileResponse approveUser(Long userId) {
        java.util.Objects.requireNonNull(userId, "User ID must not be null");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        user.setApproved(true);
        user.setActive(true);
        return toProfileResponse(userRepository.save(user));
    }

    // ── Block / Unblock user (Admin) ───────────────────────────────────────
    @SuppressWarnings("null")
    public UserProfileResponse toggleUserStatus(Long userId) {
        java.util.Objects.requireNonNull(userId, "User ID must not be null");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        user.setActive(!user.isActive());
        return toProfileResponse(userRepository.save(user));
    }

    // ── Change Role (Admin) ────────────────────────────────────────────────
    @SuppressWarnings("null")
    public UserProfileResponse changeUserRole(Long userId, Role newRole) {
        java.util.Objects.requireNonNull(userId, "User ID must not be null");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        user.setRole(newRole);
        return toProfileResponse(userRepository.save(user));
    }

    // ── Private helpers ────────────────────────────────────────────────────
    private AuthResponse buildAuthResponse(String token, User user) {
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }

    private UserProfileResponse toProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .isActive(user.isActive())
                .isApproved(user.isApproved())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
