package com.elarbiallam.order.enums;

public enum OrderStatus {
    PENDING,    // Commande créée, en attente de paiement
    PAID,       // Payée
    SHIPPED,    // Expédiée
    DELIVERED,  // Livrée
    CANCELLED   // Annulée
}