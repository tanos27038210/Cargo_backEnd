package com.cargocrm.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@JsonInclude
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String code,
        String message,
        List<FieldErrorDetail> details
) {
    public record FieldErrorDetail(
            String field,
            String message
    ) {
    }

    public static ErrorResponse of(int status, String code, String message) {
        return new ErrorResponse(
                LocalDateTime.now(),
                status,
                code,
                message,
                Collections.emptyList()
        );
    }

    public static ErrorResponse of(int status, String code, String message, List<FieldErrorDetail> details) {
        return new ErrorResponse(
                LocalDateTime.now(),
                status,
                code,
                message,
                details != null ? details : Collections.emptyList()
        );
    }
}
