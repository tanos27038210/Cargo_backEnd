package com.cargocrm.dto;

public record LoginResponse(
        String token,
        UserResponse user
) {
}
