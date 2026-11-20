package com.cargocrm.dto;

import com.cargocrm.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
        @NotBlank(message = "full name can`t be blank")
        @Size(max = 100, message = "full name can`t be more than 100 characters")
        String fullName,

        @NotBlank(message = "email can`t be blank")
        @Email(message = "email format is invalid")
        @Size(max = 150, message = "email can`t be more than 150 characters")
        String email,

        @NotBlank(message = "password can`t be blank")
        @Size(min = 8, message = "password must be at least 8 characters")
        String password,

        @NotNull(message = "role can`t be null")
        Role role
) {
}
