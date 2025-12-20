package com.elarbiallam.order.dto;
import java.math.BigDecimal;
import java.util.Set;

public record CartResponse(
        Long id,
        String userId,
        BigDecimal totalAmount,
        Set<CartItemResponse> items
) {}