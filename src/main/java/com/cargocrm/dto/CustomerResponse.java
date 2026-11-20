package com.cargocrm.dto;

import com.cargocrm.entity.Customer;

import java.time.LocalDateTime;

public record CustomerResponse(
        Long id,
        String name,
        String companyName,
        String email,
        String phone,
        String address,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getCompanyName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getAddress(),
                customer.getNotes(),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }
}
