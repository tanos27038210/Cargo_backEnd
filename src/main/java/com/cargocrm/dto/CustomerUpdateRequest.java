package com.cargocrm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CustomerUpdateRequest (
        @NotBlank(message = "name shouldn`t be empty")
        @Size(max = 150, message = "name must be at most 150 characters")
        String name,

        @Size(max = 150, message = "companyName must be at most 150 characters")
        String companyName,

        @Email(message = "wrong email format")
        @Size(max = 100, message = "email must be at most 100 characters")
        String email,

        @NotBlank(message = "phone shouldn`t be empty")
        @Size(max = 50, message = "phone must be at most 50 characters")
        @Pattern(regexp = "^[0-9+()\\s-]{5,50}$", message = "wrong number")
        String phone,

        @Size(max = 100, message = "address must be at most 100 characters")
        String address,

        @Size(max = 250, message = "notes must be at most 250 characters")
        String notes
){
}
