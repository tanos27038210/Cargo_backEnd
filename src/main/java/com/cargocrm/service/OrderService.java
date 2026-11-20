package com.cargocrm.service;

import com.cargocrm.dto.*;
import com.cargocrm.entity.*;
import com.cargocrm.exception.ConflictException;
import com.cargocrm.exception.CustomerNotFoundException;
import com.cargocrm.exception.InvalidStatusTransitionException;
import com.cargocrm.exception.OrderNotFoundException;
import com.cargocrm.pagination.PaginationUtil;
import com.cargocrm.repository.CustomerRepository;
import com.cargocrm.repository.OrderRepository;
import com.cargocrm.repository.OrderSpecification;
import com.cargocrm.security.CurrentUserService;
import com.cargocrm.security.OrderSecurityService;
import com.cargocrm.util.StatusTransitions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class OrderService {
    private static final Long DEFAULT_MANAGER_ID = 1L;

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final StatusHistoryService statusHistoryService;
    private final CurrentUserService currentUserService;
    private final OrderSecurityService orderSecurityService;

    public OrderService(OrderRepository orderRepository,
                        CustomerRepository customerRepository,
                        StatusHistoryService statusHistoryService,
                        CurrentUserService currentUserService, OrderSecurityService orderSecurityService) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.statusHistoryService = statusHistoryService;
        this.currentUserService = currentUserService;
        this.orderSecurityService = orderSecurityService;
    }

    private Order findOrderOrThrow(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order: " + id + " not found"));
    }

    private Customer findCustomerOrThrow(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer: " + id + " not found"));
    }

    private String generateOrderNumber() {
        String randomUuid = UUID.randomUUID().toString();
        String shortCode = randomUuid.substring(0, 8);

        return "ORD-" + shortCode;
    }

    @Transactional
    public OrderResponse createOrder(OrderCreateRequest request) {
        Customer customer = findCustomerOrThrow(request.customerId());
        User manager = currentUserService.getCurrentUser();

        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setCustomer(customer);
        order.setManager(manager);
        order.setOriginAddress(request.originAddress());
        order.setDestinationAddress(request.destinationAddress());
        order.setDeliveryType(request.deliveryType());
        order.setPrice(request.price());
        order.setExpectedDeliveryDate(request.expectedDeliveryDate());
        order.setNotes(request.notes());

        Order saved = orderRepository.save(order);
        return OrderResponse.from(saved);
    }

    public PageResponse<OrderResponse> getOrders(int page, int limit, String sortBy, String sortDir, OrderFilter filter) {
        List<String> permissionList = List.of("createdAt", "updatedAt", "expectedDeliveryDate", "price", "status");

        Pageable pageable = PaginationUtil.createPageable(page, limit, sortBy, sortDir, permissionList, "createdAt");

        Page<Order> orderPage = orderRepository.findAll(OrderSpecification.build(filter), pageable);

        Page<OrderResponse> responsePage = orderPage.map(OrderResponse::from);

        return PageResponse.from(responsePage);
    }

    public OrderResponse getOrderById(Long id) {
        Order order = findOrderOrThrow(id);

        return OrderResponse.from(order);
    }

    @Transactional
    public OrderResponse updateOrder(Long id, OrderUpdateRequest updateRequest) {
        Order order = findOrderOrThrow(id);

        orderSecurityService.checkCanModify(order);

        OrderStatus status = order.getStatus();
        if (status == OrderStatus.DELIVERED || status == OrderStatus.CANCELLED) {
            throw new ConflictException("Order in status " + status + " can`t be modified");
        }

        order.setOriginAddress(updateRequest.originAddress());
        order.setDestinationAddress(updateRequest.destinationAddress());
        order.setDeliveryType(updateRequest.deliveryType());
        order.setPrice(updateRequest.price());
        order.setExpectedDeliveryDate(updateRequest.expectedDeliveryDate());
        order.setNotes(updateRequest.notes());

        Order updatedOrder = orderRepository.saveAndFlush(order);

        return OrderResponse.from(updatedOrder);
    }

    @Transactional
    public void deleteOrder(Long id) {
        Order order = findOrderOrThrow(id);

        orderRepository.delete(order);
    }

    @Transactional
    public OrderResponse changeStatus(Long id, OrderStatusChangeRequest request) {
        OrderStatus newStatus = request.newStatus();
        Order order = findOrderOrThrow(id);
        OrderStatus oldStatus = order.getStatus();

        orderSecurityService.checkCanModify(order);

        boolean isCanTransition = StatusTransitions.canTransition(oldStatus, newStatus);
        boolean isRequiresAdmin = StatusTransitions.requiresAdmin(oldStatus, newStatus);

        if (!isCanTransition && !isRequiresAdmin) {
            throw new InvalidStatusTransitionException(String.format("Can`t change order status %s to %s", oldStatus, newStatus));
        }

        order.setStatus(newStatus);
        orderRepository.saveAndFlush(order);

        statusHistoryService.recordStatusChange(
                EntityType.ORDER,
                order.getId(),
                oldStatus.name(),
                newStatus.name(),
                request.comment()
        );
        return OrderResponse.from(order);
    }
}
