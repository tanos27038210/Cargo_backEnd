package com.cargocrm.repository;

import com.cargocrm.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByOrderIdOrderByIdAsc(Long orderId);
}
