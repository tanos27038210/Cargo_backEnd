package com.cargocrm.security;

import com.cargocrm.entity.Order;
import com.cargocrm.entity.Role;
import com.cargocrm.entity.User;
import org.springframework.stereotype.Component;

import org.springframework.security.access.AccessDeniedException;

@Component
public class OrderSecurityService {
    private final CurrentUserService currentUserService;

    public OrderSecurityService(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    public void checkCanModify(Order order) {
        User currentUser = currentUserService.getCurrentUser();

        if (currentUser.getRole() == Role.ADMIN) {
            return;
        }

        Long orderManagerId = order.getManager().getId();

        if (!orderManagerId.equals(currentUser.getId()))  {
            throw new AccessDeniedException("You can only update your own orders");
        }
    }
}
