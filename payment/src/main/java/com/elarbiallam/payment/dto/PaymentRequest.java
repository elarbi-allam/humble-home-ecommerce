package com.elarbiallam.payment.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    // Le seul lien nécessaire avec la commande
    private Long orderId;

    // --- Informations de paiement (selon le cas) ---
    // Ces champs sont remplis par le Frontend selon ce que l'utilisateur saisit

    // Cas: CARTE BANCAIRE
    private String cardNumber;
    private String expDate;
    private String cvv;
    private String holderName;

    // Cas: PAYPAL
    private String paypalEmail;

    // Cas: VIREMENT / COD
    private String referenceNote; // Note optionnelle
}