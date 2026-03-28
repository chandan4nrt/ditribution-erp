package com.ecomm.nrt.auth.util;

import com.ecomm.nrt.auth.entity.Role;
import com.ecomm.nrt.auth.entity.User;
import com.ecomm.nrt.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        java.util.Optional<User> existingAdmin = userRepository.findByUsername("admin");
        
        if (existingAdmin.isPresent()) {
            User admin = existingAdmin.get();
            if (!admin.isApproved() || !admin.isActive() || admin.getRole() != Role.ADMIN) {
                admin.setApproved(true);
                admin.setActive(true);
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);
                System.out.println("Existing 'admin' user updated to be Approved Administrator.");
            }
        } else {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@erp.com")
                    .fullName("System Administrator")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .isActive(true)
                    .isApproved(true)
                    .build();
            userRepository.save(admin);
            System.out.println("Default Admin created: admin / admin123");
        }
    }
}
