package com.elarbiallam.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddToCartRequest(
        @NotNull(message = "L'ID produit est obligatoire")
        Long productId,

        @Min(value = 1, message = "La quantité doit être au moins 1")
        int quantity
) {}