package com.cargocrm.controller;

import com.cargocrm.dto.MessageCreateRequest;
import com.cargocrm.dto.MessageResponse;
import com.cargocrm.entity.Message;
import com.cargocrm.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders/{orderId}/messages")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<MessageResponse> createMessage(@PathVariable Long orderId,
                                                         @RequestBody @Valid MessageCreateRequest request) {
       MessageResponse message = messageService.createMessage(orderId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @GetMapping
    public List<MessageResponse> getMessages(@PathVariable Long orderId) {
        return messageService.getMessages(orderId);
    }
}
