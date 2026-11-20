package com.cargocrm.service;

import com.cargocrm.dto.StatusHistoryResponse;
import com.cargocrm.entity.EntityType;
import com.cargocrm.entity.StatusHistory;
import com.cargocrm.entity.User;
import com.cargocrm.repository.StatusHistoryRepository;
import com.cargocrm.security.CurrentUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class StatusHistoryService {
    private final StatusHistoryRepository statusHistoryRepository;
    private final CurrentUserService currentUserService;

    public StatusHistoryService(StatusHistoryRepository statusHistoryRepository,
                                CurrentUserService currentUserService) {
        this.statusHistoryRepository = statusHistoryRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public void recordStatusChange(
            EntityType entityType,
            Long entityId,
            String oldStatus,
            String newStatus,
            String comment
    ) {
        User manager = currentUserService.getCurrentUser();

        StatusHistory history = new StatusHistory();
        history.setEntityType(entityType);
        history.setEntityId(entityId);
        history.setOldStatus(oldStatus);
        history.setNewStatus(newStatus);
        history.setChangedBy(manager);
        history.setComment(comment);

        statusHistoryRepository.save(history);
    }

    public List<StatusHistoryResponse> getOrderStatusHistory(Long orderId) {
        return statusHistoryRepository.findByEntityIdAndEntityTypeOrderByCreatedAtDesc(orderId, EntityType.ORDER)
                .stream().map(StatusHistoryResponse::from).toList();
    }
}
