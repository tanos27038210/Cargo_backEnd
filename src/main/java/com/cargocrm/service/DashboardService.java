package com.cargocrm.service;

import com.cargocrm.dto.DashboardSummaryResponse;
import com.cargocrm.dto.OrderResponse;
import com.cargocrm.entity.Order;
import com.cargocrm.entity.OrderStatus;
import com.cargocrm.entity.Role;
import com.cargocrm.entity.User;
import com.cargocrm.repository.OrderRepository;
import com.cargocrm.repository.OrderSpecification;
import com.cargocrm.security.CurrentUserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DashboardService {
    private static final int LIST_SIZE = 5;

    private final OrderRepository orderRepository;
    private final CurrentUserService currentUserService;
    private final UserService userService;

    public DashboardService(OrderRepository orderRepository,
                            CurrentUserService currentUserService,
                            UserService userService) {
        this.orderRepository = orderRepository;
        this.currentUserService = currentUserService;
        this.userService = userService;
    }

    public DashboardSummaryResponse getSummary() {
        User user = currentUserService.getCurrentUser();
        Long managerId = user.getRole() == Role.ADMIN ? null : user.getId();
        Specification<Order> owned = OrderSpecification.ownedBy(managerId);
        LocalDate today = LocalDate.now();

        Map<OrderStatus, Long> byStatus = new EnumMap<>(OrderStatus.class);
        for (OrderStatus status : OrderStatus.values()) {
            byStatus.put(status, orderRepository.count(owned.and(OrderSpecification.hasStatus(status))));
        }

        long totalOrders = byStatus.values().stream().mapToLong(Long::longValue).sum();
        long deliveredOrders = byStatus.get(OrderStatus.DELIVERED);
        long cancelledOrders = byStatus.get(OrderStatus.CANCELLED);
        long activeOrders = totalOrders - deliveredOrders - cancelledOrders;

        Specification<Order> overdueOrders = owned.and(OrderSpecification.overdue(today));
        long overdueCount = orderRepository.count(overdueOrders);

        List<OrderResponse> recentOrdersList = orderRepository.findAll(owned,
                        PageRequest.of(
                                0,
                                LIST_SIZE,
                                Sort.by(Sort.Direction.DESC, "createdAt")
                                        .and(Sort.by(Sort.Direction.DESC, "id"))))
                .map(OrderResponse::from)
                .getContent();

        List<OrderResponse> overdueOrderList = orderRepository.findAll(overdueOrders,
                        PageRequest.of(
                                0,
                                LIST_SIZE,
                                Sort.by(Sort.Direction.ASC, "expectedDeliveryDate")
                                        .and(Sort.by(Sort.Direction.ASC, "id"))))
                .map(OrderResponse::from)
                .getContent();

        return new DashboardSummaryResponse(
                totalOrders,
                deliveredOrders,
                cancelledOrders,
                activeOrders,
                overdueCount,
                byStatus,
                recentOrdersList,
                overdueOrderList
        );
    }
}
