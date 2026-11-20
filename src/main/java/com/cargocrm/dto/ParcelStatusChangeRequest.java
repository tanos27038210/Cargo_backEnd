package com.cargocrm.dto;

import com.cargocrm.entity.ParcelStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ParcelStatusChangeRequest(
        @NotNull(message = " new status can`t be empty")
        ParcelStatus newStatus,

        @Size(max = 250, message = "comment can`t be more than 250 characters")
        String comment
) {
}
