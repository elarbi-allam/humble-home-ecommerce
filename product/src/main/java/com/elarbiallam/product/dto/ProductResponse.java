package com.elarbiallam.product.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponse(
        Long id,
        String name,
        String brand,
        BigDecimal price,
        int inventory,
        String description,
        String categoryName, // Juste le nom, pas toute l'objet
        List<String> imageUrls // Juste les URLs
) {}