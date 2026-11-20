package com.cargocrm.dto;

import com.cargocrm.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @NotBlank(message = "full name can`t be blank")
        @Size(max = 100, message = "full name can`t be more 100 characters")
        String fullName,

        @NotBlank(message = "email can`t be blank")
        @Email(message = "email format is invalid")
        @Size(max = 150, message = "email can`t be more 100 characters")
        String email,

        @NotNull(message = "role can`t be null")
        Role role,

        @NotNull(message = "is active can`t be null")
        Boolean isActive
) {
}
