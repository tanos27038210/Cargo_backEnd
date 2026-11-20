package com.cargocrm.repository;

import com.cargocrm.entity.Parcel;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

public class ParcelSpecification {
    public static Specification<Parcel> build(Long orderId, String search) {
        return (root, query, cb) -> {
            List<Predicate> conditions = new ArrayList<>();
            conditions.add(cb.equal(root.get("order").get("id"), orderId));

            if (search != null && !search.trim().isEmpty()) {
                String pattern = "%" + search.trim().toLowerCase() + "%";
                conditions.add(cb.like(cb.lower(root.get("description")), pattern));
            }
            return cb.and(conditions.toArray(new Predicate[0]));
        };
    }
}
