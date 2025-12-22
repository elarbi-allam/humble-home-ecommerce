package com.elarbiallam.payment.dto;

import lombok.Data;
import com.elarbiallam.payment.model.PaymentMethod; // Assure-toi d'importer ton Enum

@Data
public class OrderResponse {
    private Long id;
    private String reference;
    private Double totalAmount; // Correspond au JSON: "totalAmount": 1000.00
    private PaymentMethod paymentMethod; // Correspond au JSON: "paymentMethod": "CREDIT_CARD"
    private String status;      // Correspond au JSON: "status": "PENDING"
    private String email;       // Utile si on veut envoyer une notif de paiement
}