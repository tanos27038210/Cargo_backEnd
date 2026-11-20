package com.cargocrm.repository;

import com.cargocrm.dto.OrderFilter;
import com.cargocrm.entity.Customer;
import com.cargocrm.entity.Order;
import com.cargocrm.entity.OrderStatus;
import com.cargocrm.entity.Parcel;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderSpecification {
    public static Specification<Order> build(OrderFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> conditions = new ArrayList<>();
            if (filter.search() != null && !filter.search().isBlank()) {
                String pattern = "%" + filter.search().trim().toLowerCase() + "%";

                Join<Order, Customer> customer = root.join("customer");

                Subquery<Long> subquery = query.subquery(Long.class);
                Root<Parcel> parcelRoot = subquery.from(Parcel.class);
                subquery.select(parcelRoot.get("id"))
                        .where(
                                cb.equal(parcelRoot.get("order"), root),
                                cb.like(cb.lower(parcelRoot.get("trackingNumber")), pattern)
                        );

                conditions.add(cb.or(
                        cb.like(cb.lower(root.get("orderNumber")), pattern),
                        cb.like(cb.lower(root.get("originAddress")), pattern),
                        cb.like(cb.lower(root.get("destinationAddress")), pattern),
                        cb.like(cb.lower(customer.get("name")), pattern),
                        cb.exists(subquery)
                ));
            }
            if (filter.status() != null) {
                conditions.add(cb.equal(root.get("status"), filter.status()));
            }
            if (filter.deliveryType() != null) {
                conditions.add(cb.equal(root.get("deliveryType"), filter.deliveryType()));
            }
            if (filter.managerId() != null) {
                conditions.add(cb.equal(root.get("manager").get("id"), filter.managerId()));
            }
            if (filter.priceMin() != null) {
                conditions.add(cb.greaterThanOrEqualTo(root.get("price"), filter.priceMin()));
            }
            if (filter.priceMax() != null) {
                conditions.add(cb.lessThanOrEqualTo(root.get("price"), filter.priceMax()));
            }
            if (filter.expectedFrom() != null) {
                conditions.add(cb.greaterThanOrEqualTo(root.get("expectedDeliveryDate"), filter.expectedFrom()));
            }
            if (filter.expectedTo() != null) {
                conditions.add(cb.lessThanOrEqualTo(root.get("expectedDeliveryDate"), filter.expectedTo()));
            }
            if (filter.createdFrom() != null) {
                conditions.add(cb.greaterThanOrEqualTo(root.get("createdAt"), filter.createdFrom().atStartOfDay()));
            }
            if (filter.createdTo() != null) {
                conditions.add(cb.lessThan(root.get("createdAt"), filter.createdTo().plusDays(1).atStartOfDay()));
            }

            if (filter.customerId() != null) {
                conditions.add(cb.equal(root.get("customer").get("id"), filter.customerId()));
            }

            return cb.and(conditions.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }

    public static Specification<Order> ownedBy(Long managerId) {
        return (root, query, cb) -> managerId == null
                ? cb.conjunction()
                : cb.equal(root.get("manager").get("id"), managerId);
    }

    public static Specification<Order> hasStatus(OrderStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Order> overdue(LocalDate today) {
        return (root, query, cb) -> cb.and(
                cb.lessThan(root.<LocalDate>get("expectedDeliveryDate"), today),
                cb.not(root.get("status").in(OrderStatus.DELIVERED, OrderStatus.CANCELLED))
        );
    }
}
