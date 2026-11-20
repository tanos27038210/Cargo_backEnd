package com.cargocrm.controller;

import com.cargocrm.dto.*;
import com.cargocrm.entity.DeliveryType;
import com.cargocrm.entity.OrderStatus;
import com.cargocrm.service.OrderService;
import com.cargocrm.service.StatusHistoryService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    private final StatusHistoryService statusHistoryService;

    public OrderController(OrderService orderService, StatusHistoryService statusHistoryService) {
        this.orderService = orderService;
        this.statusHistoryService = statusHistoryService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody @Valid OrderCreateRequest request) {
        OrderResponse created = orderService.createOrder(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public PageResponse<OrderResponse> getOrders(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int limit,
                                                 @RequestParam(defaultValue = "createdAt") String sortBy,
                                                 @RequestParam(defaultValue = "desc") String sortDir,
                                                 @RequestParam(required = false) String search,
                                                 @RequestParam(required = false) OrderStatus status,
                                                 @RequestParam(required = false) DeliveryType deliveryType,
                                                 @RequestParam(required = false) Long managerId,
                                                 @RequestParam(required = false) BigDecimal priceMin,
                                                 @RequestParam(required = false) BigDecimal priceMax,
                                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expectedFrom,
                                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expectedTo,
                                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdFrom,
                                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate createdTo,
                                                 @RequestParam(required = false) Long customerId
    ) {
        OrderFilter filter = new OrderFilter(
                search,
                status,
                deliveryType,
                managerId,
                priceMin,
                priceMax,
                expectedFrom,
                expectedTo,
                createdFrom,
                createdTo,
                customerId
        );
        return orderService.getOrders(page, limit, sortBy, sortDir, filter);
    }

    @GetMapping("/{id}")
    public OrderResponse getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> updateOrder(@PathVariable Long id,
                                                     @RequestBody @Valid OrderUpdateRequest updateRequest) {
        OrderResponse updated = orderService.updateOrder(id, updateRequest);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponse> changeStatus(@PathVariable Long id,
                                                      @RequestBody @Valid OrderStatusChangeRequest request) {
        OrderResponse changedStatus = orderService.changeStatus(id, request);

        return ResponseEntity.ok(changedStatus);
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<StatusHistoryResponse>> getOrderStatusHistory(@PathVariable Long id) {
        return ResponseEntity.ok(statusHistoryService.getOrderStatusHistory(id));
    }
}
