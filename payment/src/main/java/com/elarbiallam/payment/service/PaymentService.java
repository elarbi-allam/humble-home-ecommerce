package com.elarbiallam.payment.service;

import com.elarbiallam.payment.client.OrderClient;
import com.elarbiallam.payment.dto.OrderResponse;
import com.elarbiallam.payment.dto.PaymentRequest;
import com.elarbiallam.payment.model.Payment;
import com.elarbiallam.payment.model.PaymentMethod; // Import indispensable pour le switch
import com.elarbiallam.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository repository;
    private final OrderClient orderClient;

    @Transactional
    public Payment processPayment(PaymentRequest request) {
        // 1. Récupérer la commande (Source de vérité)
        // On ne fait pas confiance au frontend pour le montant ou la méthode
        OrderResponse order = orderClient.getOrderById(request.getOrderId());

        // 2. Vérifications de sécurité
        if (order == null) {
            throw new RuntimeException("Commande introuvable avec l'ID : " + request.getOrderId());
        }

        // Vérifie si la commande est déjà payée (adapte "PAID" selon ton enum OrderStatus)
        if ("PAID".equals(order.getStatus())) {
            throw new RuntimeException("Cette commande a déjà été payée.");
        }

        // 3. Validation des données spécifiques (Carte vs PayPal vs COD)
        validatePaymentDetails(request, order);

        // 4. Création de l'enregistrement de paiement
        Payment payment = Payment.builder()
                .orderId(order.getId())
                .paymentMethod(order.getPaymentMethod()) // On utilise la méthode définie dans la commande
                .amount(order.getTotalAmount())          // On utilise le montant défini dans la commande
                .currency("EUR")                         // Devise (fixe ou dynamique selon ton besoin)
                .transactionReference(UUID.randomUUID().toString()) // Simulation d'une ref bancaire
                .status("SUCCESS")
                .paymentDate(java.time.LocalDateTime.now())
                .build();

        Payment savedPayment = repository.save(payment);

        // 5. Notification au Order-Service pour passer la commande à "PAID"
        try {
            orderClient.updateOrderStatus(order.getId());
        } catch (Exception e) {
            // Log d'erreur critique : L'argent est pris, mais la commande n'a pas changé de statut.
            // Dans un système réel, on utiliserait un mécanisme de retry (Kafka/RabbitMQ).
            System.err.println("ERREUR CRITIQUE : Paiement validé mais statut commande " + order.getId() + " non mis à jour.");
            e.printStackTrace();
        }

        return savedPayment;
    }

    /**
     * Vérifie que le request contient les infos nécessaires pour la méthode choisie
     */
    private void validatePaymentDetails(PaymentRequest request, OrderResponse order) {
        // CORRECTION IMPORTANTE : Dans un switch, on utilise directement le nom de la constante (ex: CREDIT_CARD)
        // et non pas le nom qualifié (ex: PaymentMethod.CREDIT_CARD est interdit ici).
        switch (order.getPaymentMethod()) {
            case CREDIT_CARD:
            case VISA:
            case MASTERCARD:
                if (request.getCardNumber() == null || request.getCardNumber().isBlank()) {
                    throw new RuntimeException("Le numéro de carte est obligatoire pour le paiement par carte.");
                }
                // Simulation : On log juste qu'on a reçu le numéro
                System.out.println("Traitement paiement Carte Bancaire : " + request.getCardNumber());
                break;

            case PAYPAL:
                if (request.getPaypalEmail() == null || request.getPaypalEmail().isBlank()) {
                    throw new RuntimeException("L'email PayPal est obligatoire.");
                }
                System.out.println("Traitement paiement PayPal : " + request.getPaypalEmail());
                break;

            case COD: // Cash On Delivery (Paiement à la livraison)
                // Pas de validation de données requise car le paiement se fait physiquement plus tard.
                // On accepte la demande de "validation" de la commande.
                System.out.println("Traitement Cash On Delivery : Paiement à prévoir à la livraison.");
                break;

            case BITCOIN: // Exemple si tu as ajouté d'autres méthodes
            case BANK_TRANSFER:
                // Logique spécifique si nécessaire
                break;

            default:
                throw new RuntimeException("Méthode de paiement non supportée : " + order.getPaymentMethod());
        }
    }
}