package me.xjanua.spring.backend.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.xjanua.spring.backend.enums.UserStatus;
import me.xjanua.spring.backend.model.Role;
import me.xjanua.spring.backend.model.User;
import me.xjanua.spring.backend.model.UserRole;
import me.xjanua.spring.backend.repository.RoleRepository;
import me.xjanua.spring.backend.repository.UserRepository;
import me.xjanua.spring.backend.repository.UserRoleRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final String DEFAULT_PASSWORD = "123456Az@";
    private static final String ADMIN_EMAIL = "admin@gmail.com";
    private static final String USER_EMAIL = "user@gmail.com";

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Checking and initializing default data...");
        Role adminRole = getOrCreateRole("ADMIN");
        Role userRole = getOrCreateRole("USER");

        User admin = getOrCreateUser(ADMIN_EMAIL, "Administrator");
        User user = getOrCreateUser(USER_EMAIL, "Standard User");

        assignRole(admin, adminRole);
        assignRole(user, userRole);
        log.info("Data initialization completed.");
    }

    private Role getOrCreateRole(String code) {
        return roleRepository.findByCode(code)
                .orElseGet(() -> {
                    log.info("Creating role: {}", code);
                    return roleRepository.save(Role.builder().code(code).build());
                });
    }

    private User getOrCreateUser(String email, String name) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    log.info("Creating user: {}", email);
                    return userRepository.save(User.builder()
                            .email(email)
                            .name(name)
                            .password(passwordEncoder.encode(DEFAULT_PASSWORD))
                            .status(UserStatus.ACTIVE)
                            .build());
                });
    }

    private void assignRole(User user, Role role) {
        if (!userRoleRepository.existsByUserAndRole(user, role)) {
            log.info("Assigning role {} to user {}", role.getCode(), user.getEmail());
            userRoleRepository.save(UserRole.builder()
                    .user(user)
                    .role(role)
                    .build());
        }
    }
}
