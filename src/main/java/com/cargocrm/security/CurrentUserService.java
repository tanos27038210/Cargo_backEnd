package com.cargocrm.security;

import com.cargocrm.entity.Role;
import com.cargocrm.entity.User;
import com.cargocrm.exception.UnauthorizedException;
import com.cargocrm.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserService {
    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new UnauthorizedException("Authentication is required");
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UnauthorizedException("Authentication is required"));
    }

    public boolean isAdmin() {
        User user = getCurrentUser();
        return user.getRole().equals(Role.ADMIN);
    }
}
