package com.cargocrm.dto;

import com.cargocrm.entity.DeliveryType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OrderCreateRequest(
        @NotNull(message = "customer can`t be null")
        Long customerId,

        @NotBlank(message = "origin address can`t be empty")
        @Size(max = 100, message = "origin address can`t be more than 100 characters")
        String originAddress,

        @NotBlank(message = "destination address can`t be empty")
        @Size(max = 100, message = "destination address can`t be more than 100 characters")
        String destinationAddress,

        @NotNull(message = "delivery type can`t be empty")
        DeliveryType deliveryType,

        @DecimalMin(value = "0.0", message = "price can`t be negative")
        @NotNull(message = "price can`t be empty")
        @Digits(integer = 8, fraction = 2, message = "price format is invalid")
        BigDecimal price,

        @FutureOrPresent(message = "expected delivery date can`t be past")
        @NotNull(message = "expected delivery date can`t be empty")
        LocalDate expectedDeliveryDate,

        @Size(max = 250, message = "notes can`t be more than 250 characters")
        String notes
) {
}
