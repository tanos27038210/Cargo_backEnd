package com.cargocrm.dto;

import com.cargocrm.entity.OrderStatus;
import com.cargocrm.entity.StatusHistory;

import java.time.LocalDateTime;

public record StatusHistoryResponse(
        Long id,
        OrderStatus olStatus,
        OrderStatus newStatus,
        String changedByName,
        String comment,
        LocalDateTime createdAt
) {
    public static StatusHistoryResponse from(StatusHistory history) {
        return new StatusHistoryResponse(
                history.getId(),
                history.getOldStatus() != null ? OrderStatus.valueOf(history.getOldStatus()) : null,
                OrderStatus.valueOf(history.getNewStatus()),
                history.getChangedBy() != null ? history.getChangedBy().getFullName() : "System",
                history.getComment(),
                history.getCreatedAt()
        );
    }
}
