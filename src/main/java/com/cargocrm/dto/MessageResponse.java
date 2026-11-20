package com.cargocrm.dto;

import com.cargocrm.entity.Message;
import com.cargocrm.entity.Role;

import java.time.LocalDateTime;

public record MessageResponse(
        Long id,
        Long orderId,
        Long authorId,
        String authorName,
        Role authorRole,
        String text,
        LocalDateTime createdAt,
        LocalDateTime updateAt
) {
    public static MessageResponse from(Message message) {
        return new MessageResponse(
                message.getId(),
                message.getOrder().getId(),
                message.getAuthor().getId(),
                message.getAuthor().getFullName(),
                message.getAuthor().getRole(),
                message.getText(),
                message.getCreatedAt(),
                message.getUpdatedAt()
        );
    }
}
