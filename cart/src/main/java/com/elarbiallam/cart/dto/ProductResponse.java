package com.elarbiallam.cart.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponse(
        Long id,
        String name,
        BigDecimal price,
        int inventory,
        List<String> imageUrls
) {}