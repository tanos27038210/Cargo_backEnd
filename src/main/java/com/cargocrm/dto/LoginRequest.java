package com.cargocrm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "email can`t be blank")
        @Email(message = "email format is invalid")
        String email,

        @NotBlank(message = "password can`t be blank")
        String password
) {
}
