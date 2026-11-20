package com.cargocrm.dto;

import com.cargocrm.entity.DeliveryType;
import com.cargocrm.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OrderFilter(
        String search,
        OrderStatus status,
        DeliveryType deliveryType,
        Long managerId,
        BigDecimal priceMin,
        BigDecimal priceMax,
        LocalDate expectedFrom,
        LocalDate expectedTo,
        LocalDate createdFrom,
        LocalDate createdTo,
        Long customerId
) {
}
