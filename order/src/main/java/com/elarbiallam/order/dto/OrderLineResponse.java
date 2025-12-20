package com.elarbiallam.order.dto;

import java.math.BigDecimal;

public record OrderLineResponse(
        Long productId,
        String productName,
        int quantity,
        BigDecimal price
) {}