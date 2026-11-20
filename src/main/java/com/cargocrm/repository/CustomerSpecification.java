package com.cargocrm.repository;

import com.cargocrm.entity.Customer;
import org.springframework.data.jpa.domain.Specification;

public class CustomerSpecification {
    public static Specification<Customer> search(String keyword) {
        String pattern = "%" + keyword.toLowerCase() + "%";

        return (root, query, criteriaBuilder) -> criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), pattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), pattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("phone")), pattern),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("companyName")), pattern)
        );
    }
}
