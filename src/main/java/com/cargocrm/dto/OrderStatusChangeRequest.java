package com.cargocrm.dto;

import com.cargocrm.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OrderStatusChangeRequest(
        @NotNull(message = "new status can`t be empty")
        OrderStatus newStatus,

        @Size(max = 250, message = "comment can`t be bigger than 250 characters")
        String comment
) {
}
