package com.cargocrm.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ParcelUpdateRequest(
        @NotBlank(message = "description can`t be empty")
        @Size(max = 100, message = "description can`t be more than 100 characters")
        String description,

        @NotNull(message = "weight can`t be empty")
        @Positive(message = "weight must be greater than zero")
        @Digits(integer = 6, fraction = 2, message = "weight is invalid")
        BigDecimal weightKg,

        @Positive(message = "length must be greater than zero")
        @Digits(integer = 6, fraction = 2, message = "length is invalid")
        BigDecimal lengthCm,

        @Positive(message = "width must be greater than zero")
        @Digits(integer = 6, fraction = 2, message = "width is invalid")
        BigDecimal widthCm,

        @Positive(message = "height must be greater than zero")
        @Digits(integer = 6, fraction = 2, message = "height is invalid")
        BigDecimal heightCm,

        @PositiveOrZero(message = "declared value can`t be negative")
        @Digits(integer = 8, fraction = 2, message = "declared value is invalid")
        BigDecimal declaredValue,

        @NotNull(message = "fragile can`t be empty")
        Boolean fragile
) {
}
