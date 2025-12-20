package com.elarbiallam.order.dto;

import com.elarbiallam.order.enums.OrderStatus;
import com.elarbiallam.order.enums.PaymentMethod;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String reference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        OrderStatus status,
        LocalDateTime createdAt,

        // Infos pour l'Admin
        String userId,
        String email,

        List<OrderLineResponse> items
) {}