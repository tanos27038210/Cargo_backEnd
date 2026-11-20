package com.cargocrm.service;

import com.cargocrm.dto.LoginRequest;
import com.cargocrm.dto.LoginResponse;
import com.cargocrm.dto.UserResponse;
import com.cargocrm.entity.User;
import com.cargocrm.exception.UnauthorizedException;
import com.cargocrm.repository.UserRepository;
import com.cargocrm.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        String validateEmail = request.email().trim().toLowerCase();

        User user = userRepository.findByEmail(validateEmail)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        if (user.getActive() == false) {
            throw new UnauthorizedException("Account is disabled");
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

        return new LoginResponse(token, UserResponse.from(user));
    }
}
