package com.cargocrm.dto;

import com.cargocrm.entity.Parcel;
import com.cargocrm.entity.ParcelStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ParcelResponse(
        Long id,
        Long orderId,
        String orderNumber,
        String trackingNumber,
        String description,
        BigDecimal weightKg,
        BigDecimal lengthCm,
        BigDecimal widthCm,
        BigDecimal heightCm,
        BigDecimal declaredValue,
        Boolean fragile,
        ParcelStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ParcelResponse from(Parcel parcel) {
        return new ParcelResponse(
                parcel.getId(),
                parcel.getOrder().getId(),
                parcel.getOrder().getOrderNumber(),
                parcel.getTrackingNumber(),
                parcel.getDescription(),
                parcel.getWeightKg(),
                parcel.getLengthCm(),
                parcel.getWidthCm(),
                parcel.getHeightCm(),
                parcel.getDeclaredValue(),
                parcel.getFragile(),
                parcel.getStatus(),
                parcel.getCreatedAt(),
                parcel.getUpdatedAt()
        );
    }
}
