package com.cargocrm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MessageCreateRequest(
        @NotBlank(message = "text can`t be empty")
        @Size(max = 1000, message = "text can`t be more than 1000 characters")
        String text
) {
}
