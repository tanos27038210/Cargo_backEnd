package com.cargocrm.service;

import com.cargocrm.dto.ParcelCreateRequest;
import com.cargocrm.dto.ParcelResponse;
import com.cargocrm.dto.ParcelStatusChangeRequest;
import com.cargocrm.dto.ParcelUpdateRequest;
import com.cargocrm.entity.*;
import com.cargocrm.exception.ConflictException;
import com.cargocrm.exception.InvalidStatusTransitionException;
import com.cargocrm.exception.OrderNotFoundException;
import com.cargocrm.exception.ParcelNotFoundException;
import com.cargocrm.repository.OrderRepository;
import com.cargocrm.repository.ParcelRepository;
import com.cargocrm.repository.ParcelSpecification;
import com.cargocrm.security.CurrentUserService;
import com.cargocrm.security.OrderSecurityService;
import com.cargocrm.util.StatusTransitions;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ParcelService {
    private final ParcelRepository parcelRepository;
    private final OrderRepository orderRepository;
    private final StatusHistoryService statusHistoryService;
    private final OrderSecurityService orderSecurityService;
    private final CurrentUserService currentUserService;

    public ParcelService(ParcelRepository parcelRepository,
                         OrderRepository orderRepository,
                         StatusHistoryService statusHistoryService,
                         CurrentUserService currentUserService,
                         OrderSecurityService orderSecurityService) {
        this.parcelRepository = parcelRepository;
        this.orderRepository = orderRepository;
        this.statusHistoryService = statusHistoryService;
        this.orderSecurityService = orderSecurityService;
        this.currentUserService = currentUserService;
    }

    private Order findOrderOrThrow(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order: " + id + " not found"));
    }

    private Parcel findParcelOrThrow(Long id) {
        return parcelRepository.findById(id)
                .orElseThrow(() -> new ParcelNotFoundException("Parcel: " + id + " not found"));
    }

    private String generateTrackingNumber() {
        String randomUuid = UUID.randomUUID().toString();
        String shortCode = randomUuid.substring(0, 8);

        return "TRK-" + shortCode;
    }

    @Transactional
    public ParcelResponse createParcel(Long orderId, ParcelCreateRequest request) {
        Order order = findOrderOrThrow(orderId);
        orderSecurityService.checkCanModify(order);

        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new ConflictException("Cannot add parcels to an order in status " + order.getStatus());
        }

        Parcel parcel = new Parcel();
        parcel.setOrder(order);
        parcel.setTrackingNumber(generateTrackingNumber());
        parcel.setDescription(request.description());
        parcel.setWeightKg(request.weightKg());
        parcel.setLengthCm(request.lengthCm());
        parcel.setWidthCm(request.widthCm());
        parcel.setHeightCm(request.heightCm());
        parcel.setDeclaredValue(request.declaredValue());
        parcel.setFragile(request.fragile());

        Parcel saved = parcelRepository.saveAndFlush(parcel);
        return ParcelResponse.from(saved);
    }

    public List<ParcelResponse> getParcels(Long orderId, String search) {
        findOrderOrThrow(orderId);

        return parcelRepository.findAll(ParcelSpecification.build(orderId, search)).stream()
                .map(ParcelResponse::from).toList();
    }

    public ParcelResponse getParcelById(Long id) {
        Parcel parcel = findParcelOrThrow(id);

        return ParcelResponse.from(parcel);
    }

    @Transactional
    public ParcelResponse updateParcel(Long id, ParcelUpdateRequest request) {
        Parcel parcel = findParcelOrThrow(id);

        orderSecurityService.checkCanModify(parcel.getOrder());

        parcel.setDescription(request.description());
        parcel.setWeightKg(request.weightKg());
        parcel.setLengthCm(request.lengthCm());
        parcel.setWidthCm(request.widthCm());
        parcel.setHeightCm(request.heightCm());
        parcel.setDeclaredValue(request.declaredValue());
        parcel.setFragile(request.fragile());

        Parcel updated = parcelRepository.saveAndFlush(parcel);
        return ParcelResponse.from(updated);
    }

    @Transactional
    public void deleteParcel(Long id) {
        Parcel parcel = findParcelOrThrow(id);

        parcelRepository.delete(parcel);
    }

    @Transactional
    public ParcelResponse changeStatus(Long id, ParcelStatusChangeRequest request) {
        ParcelStatus newStatus = request.newStatus();
        Parcel parcel = findParcelOrThrow(id);
        ParcelStatus oldStatus = parcel.getStatus();

        orderSecurityService.checkCanModify(parcel.getOrder());

        boolean isCanTransition = StatusTransitions.canTransition(oldStatus, newStatus);
        boolean isRequiresAdmin = StatusTransitions.requiresAdmin(oldStatus, newStatus);

        if (!isCanTransition && !isRequiresAdmin) {
            throw new InvalidStatusTransitionException("Invalid status transitions");
        }
        if (isRequiresAdmin && !currentUserService.isAdmin()) {
            throw new AccessDeniedException("Only admin can perform this status change");
        }
        parcel.setStatus(newStatus);
        parcelRepository.saveAndFlush(parcel);

        statusHistoryService.recordStatusChange(
                EntityType.PARCEL,
                parcel.getId(),
                oldStatus.name(),
                newStatus.name(),
                request.comment()
        );
        return ParcelResponse.from(parcel);
    }
}
