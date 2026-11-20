package com.cargocrm.dto;

import com.cargocrm.entity.OrderStatus;

import java.util.List;
import java.util.Map;

public record DashboardSummaryResponse(
        long totalOrders,
        long deliveredOrders,
        long cancelledOrders,
        long activeOrders,
        long overdueOrders,
        Map<OrderStatus, Long> ordersByStatus,
        List<OrderResponse> recentOrdersList,
        List<OrderResponse> overdueOrderList
) {
}
