package com.cargocrm.service;

import com.cargocrm.dto.PageResponse;
import com.cargocrm.dto.UserCreateRequest;
import com.cargocrm.dto.UserResponse;
import com.cargocrm.dto.UserUpdateRequest;
import com.cargocrm.entity.User;
import com.cargocrm.exception.ConflictException;
import com.cargocrm.exception.UserDeactivateException;
import com.cargocrm.exception.UserNotFoundException;
import com.cargocrm.pagination.PaginationUtil;
import com.cargocrm.repository.UserRepository;
import com.cargocrm.security.CurrentUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserService currentUserService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       CurrentUserService currentUserService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.currentUserService = currentUserService;
    }

    private User findUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User: " + id + " not found"));
    }

    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        String validateEmail = request.email().trim().toLowerCase();

        if (userRepository.existsByEmail(validateEmail)) {
            throw new ConflictException("Email already exists");
        }

        User user = new User();
        user.setFullName(request.fullName());
        user.setEmail(validateEmail);
        user.setRole(request.role());

        String hashPassword = passwordEncoder.encode(request.password());
        user.setPasswordHash(hashPassword);

        User savedUser = userRepository.saveAndFlush(user);

        return UserResponse.from(savedUser);
    }

    public PageResponse<UserResponse> getAllUsers(int page, int limit, String sortBy, String sortDir) {
        List<String> permissionList = List.of("createdAt", "updatedAt", "fullName", "email");

        Pageable pageable = PaginationUtil.createPageable(page, limit, sortBy, sortDir, permissionList, "createdAt");

        Page<User> userPage = userRepository.findAll(pageable);

        Page<UserResponse> responsePage = userPage.map(UserResponse::from);

        return PageResponse.from(responsePage);
    }

    public UserResponse getUserById(Long id) {
        User user = findUserOrThrow(id);

        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User updatedUser = findUserOrThrow(id);
        String validateEmail = request.email().trim().toLowerCase();
        String currentEmail = currentUserService.getCurrentUser().getEmail();

        if (!updatedUser.getEmail().equals(validateEmail)) {
            if (userRepository.existsByEmail(validateEmail)) {
                throw new ConflictException("Email already exists");
            }
        }

        if (updatedUser.getEmail().equals(currentEmail)) {
            if (request.isActive() != null && !request.isActive()) {
                throw new UserDeactivateException("You can`t deactivate your own account");
            }
            if (request.role() != null && !request.role().equals(updatedUser.getRole())) {
                throw new UserDeactivateException("You cannot change your own role");
            }
        }

        updatedUser.setFullName(request.fullName());
        updatedUser.setEmail(validateEmail);
        updatedUser.setRole(request.role());
        updatedUser.setActive(request.isActive());

        userRepository.saveAndFlush(updatedUser);

        return UserResponse.from(updatedUser);
    }

    @Transactional
    public void deactivateUser(Long id, String currentAdminEmail) {
        User user = findUserOrThrow(id);

        if (user.getEmail().equals(currentAdminEmail)) {
            throw new UserDeactivateException("You can`t deactivate your own account");
        }
        user.setActive(false);

        userRepository.saveAndFlush(user);
    }

    @Transactional
    public void activateUser(Long id) {
        User user = findUserOrThrow(id);

        user.setActive(true);

        userRepository.save(user);
    }
}
