package com.cargocrm.dto;

import com.cargocrm.entity.DeliveryType;
import com.cargocrm.entity.Order;
import com.cargocrm.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record OrderResponse (
        Long id,
        String orderNumber,
        Long customerId,
        String customerName,
        Long managerId,
        String managerFullName,
        String originAddress,
        String destinationAddress,
        DeliveryType deliveryType,
        BigDecimal price,
        OrderStatus status,
        LocalDate expectedDeliveryDate,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
){
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getCustomer().getId(),
                order.getCustomer().getName(),
                order.getManager().getId(),
                order.getManager().getFullName(),
                order.getOriginAddress(),
                order.getDestinationAddress(),
                order.getDeliveryType(),
                order.getPrice(),
                order.getStatus(),
                order.getExpectedDeliveryDate(),
                order.getNotes(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }
}
