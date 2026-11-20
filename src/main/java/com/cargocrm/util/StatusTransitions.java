package com.cargocrm.util;

import com.cargocrm.entity.OrderStatus;
import com.cargocrm.entity.ParcelStatus;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public class StatusTransitions {
    private static final Map<OrderStatus, Set<OrderStatus>> ORDER_TRANSITIONS = new EnumMap<>(OrderStatus.class);
    private static final Map<ParcelStatus, Set<ParcelStatus>> PARCEL_TRANSITIONS = new EnumMap<>(ParcelStatus.class);
    private static final Map<OrderStatus, Set<OrderStatus>> ADMIN_ORDER_TRANSITIONS = new EnumMap<>(OrderStatus.class);
    private static final Map<ParcelStatus, Set<ParcelStatus>> ADMIN_PARCEL_TRANSITIONS = new EnumMap<>(ParcelStatus.class);

    public StatusTransitions() {
    }

    static {
        ORDER_TRANSITIONS.put(OrderStatus.DRAFT, Set.of(
                OrderStatus.CONFIRMED,
                OrderStatus.CANCELLED));

        ORDER_TRANSITIONS.put(OrderStatus.CONFIRMED, Set.of(
                OrderStatus.PICKUP_SCHEDULED,
                OrderStatus.CANCELLED));

        ORDER_TRANSITIONS.put(OrderStatus.PICKUP_SCHEDULED, Set.of(
                OrderStatus.IN_TRANSIT,
                OrderStatus.CANCELLED));

        ORDER_TRANSITIONS.put(OrderStatus.IN_TRANSIT, Set.of(
                OrderStatus.AT_WAREHOUSE,
                OrderStatus.OUT_FOR_DELIVERY,
                OrderStatus.RETURNED));

        ORDER_TRANSITIONS.put(OrderStatus.AT_WAREHOUSE, Set.of(
                OrderStatus.OUT_FOR_DELIVERY,
                OrderStatus.RETURNED));

        ORDER_TRANSITIONS.put(OrderStatus.OUT_FOR_DELIVERY, Set.of(
                OrderStatus.DELIVERED,
                OrderStatus.RETURNED));

        ORDER_TRANSITIONS.put(OrderStatus.RETURNED, Set.of(OrderStatus.CANCELLED));
        ORDER_TRANSITIONS.put(OrderStatus.DELIVERED, Set.of());
        ORDER_TRANSITIONS.put(OrderStatus.CANCELLED, Set.of());


        PARCEL_TRANSITIONS.put(ParcelStatus.REGISTERED, Set.of(ParcelStatus.PICKED_UP));

        PARCEL_TRANSITIONS.put(ParcelStatus.PICKED_UP, Set.of(
                ParcelStatus.IN_TRANSIT,
                ParcelStatus.DAMAGED,
                ParcelStatus.LOST));

        PARCEL_TRANSITIONS.put(ParcelStatus.IN_TRANSIT, Set.of(
                ParcelStatus.SORTING_CENTER,
                ParcelStatus.OUT_FOR_DELIVERY,
                ParcelStatus.DAMAGED,
                ParcelStatus.LOST,
                ParcelStatus.RETURNED));

        PARCEL_TRANSITIONS.put(ParcelStatus.SORTING_CENTER, Set.of(
                ParcelStatus.IN_TRANSIT,
                ParcelStatus.OUT_FOR_DELIVERY,
                ParcelStatus.DAMAGED,
                ParcelStatus.LOST,
                ParcelStatus.RETURNED));

        PARCEL_TRANSITIONS.put(ParcelStatus.OUT_FOR_DELIVERY, Set.of(
                ParcelStatus.DELIVERED,
                ParcelStatus.DAMAGED,
                ParcelStatus.LOST,
                ParcelStatus.RETURNED));

        PARCEL_TRANSITIONS.put(ParcelStatus.DAMAGED, Set.of(ParcelStatus.RETURNED));
        PARCEL_TRANSITIONS.put(ParcelStatus.DELIVERED, Set.of());
        PARCEL_TRANSITIONS.put(ParcelStatus.RETURNED, Set.of());
        PARCEL_TRANSITIONS.put(ParcelStatus.LOST, Set.of());

        ADMIN_ORDER_TRANSITIONS.put(OrderStatus.IN_TRANSIT, Set.of(OrderStatus.CANCELLED));

        ADMIN_PARCEL_TRANSITIONS.put(ParcelStatus.LOST, Set.of(ParcelStatus.OUT_FOR_DELIVERY));
    }

    public static boolean canTransition(OrderStatus current, OrderStatus next) {
        Set<OrderStatus> allowedNextStatuses = ORDER_TRANSITIONS.getOrDefault(current, Set.of());

        return allowedNextStatuses.contains(next);
    }

    public static boolean canTransition(ParcelStatus current, ParcelStatus next) {
        Set<ParcelStatus> allowedNextStatuses = PARCEL_TRANSITIONS.getOrDefault(current, Set.of());

        return allowedNextStatuses.contains(next);
    }

    public static boolean requiresAdmin(OrderStatus current, OrderStatus next) {
        Set<OrderStatus> nextStatuses = ADMIN_ORDER_TRANSITIONS.getOrDefault(current, Set.of());

        return nextStatuses.contains(next);
    }

    public static boolean requiresAdmin(ParcelStatus current, ParcelStatus next) {
        Set<ParcelStatus> nextStatuses = ADMIN_PARCEL_TRANSITIONS.getOrDefault(current, Set.of());

        return nextStatuses.contains(next);
    }
}
