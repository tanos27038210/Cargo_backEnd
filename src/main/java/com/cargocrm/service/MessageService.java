package com.cargocrm.service;

import com.cargocrm.dto.MessageCreateRequest;
import com.cargocrm.dto.MessageResponse;
import com.cargocrm.entity.Message;
import com.cargocrm.entity.Order;
import com.cargocrm.exception.OrderNotFoundException;
import com.cargocrm.repository.MessageRepository;
import com.cargocrm.repository.OrderRepository;
import com.cargocrm.security.CurrentUserService;
import com.cargocrm.security.OrderSecurityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MessageService {
    private final MessageRepository messageRepository;
    private final OrderRepository orderRepository;
    private final OrderSecurityService orderSecurityService;
    private final CurrentUserService currentUserService;

    public MessageService(MessageRepository messageRepository,
                          OrderRepository orderRepository,
                          OrderSecurityService orderSecurityService,
                          CurrentUserService currentUserService) {
        this.messageRepository = messageRepository;
        this.orderRepository = orderRepository;
        this.orderSecurityService = orderSecurityService;
        this.currentUserService = currentUserService;
    }

    private Order findOrderOrThrow(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order with id: " + id + " not found"));
    }

    @Transactional
    public MessageResponse createMessage(Long id,
                                         MessageCreateRequest request) {
        Order order = findOrderOrThrow(id);
        orderSecurityService.checkCanModify(order);

        Message message = new Message();
        message.setOrder(order);
        message.setAuthor(currentUserService.getCurrentUser());
        message.setText(request.text().trim());

        Message saved = messageRepository.saveAndFlush(message);
        return MessageResponse.from(saved);
    }

    public List<MessageResponse> getMessages(Long orderId) {
        Order order = findOrderOrThrow(orderId);
        orderSecurityService.checkCanModify(order);
        return messageRepository.findByOrderIdOrderByIdAsc(orderId)
                .stream()
                .map(MessageResponse::from)
                .toList();
    }
}
