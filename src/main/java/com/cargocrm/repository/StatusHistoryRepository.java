package com.cargocrm.repository;

import com.cargocrm.entity.EntityType;
import com.cargocrm.entity.StatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatusHistoryRepository extends JpaRepository<StatusHistory, Long> {
    List<StatusHistory> findByEntityIdAndEntityTypeOrderByCreatedAtDesc(Long entityId, EntityType entityType);
}
