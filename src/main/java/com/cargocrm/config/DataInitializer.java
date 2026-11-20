package com.cargocrm.config;

import com.cargocrm.entity.Role;
import com.cargocrm.entity.User;
import com.cargocrm.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev", "prod"})
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        createIfMissing("Admin", "admin@cargo.com", "Admin12345", Role.ADMIN);
        createIfMissing("Manager", "manager@cargo.com", "Manager12345", Role.MANAGER);
    }

    private void createIfMissing(String fullName, String email, String password, Role role) {
        if (userRepository.existsByEmail(email)) {
            return;
        }

        User user = new User();
//        User user = userRepository.findByEmail(email).orElse(new User());
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role);
        user.setActive(true);
        userRepository.save(user);
    }
}
