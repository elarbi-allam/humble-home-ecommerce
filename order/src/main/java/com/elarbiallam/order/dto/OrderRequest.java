package com.elarbiallam.order.dto;

import com.elarbiallam.order.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public record OrderRequest(
        @NotNull(message = "La méthode de paiement est obligatoire")
        PaymentMethod paymentMethod
) {}