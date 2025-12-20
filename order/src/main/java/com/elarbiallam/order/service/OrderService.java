package com.elarbiallam.order.service;

import com.elarbiallam.order.client.CartClient;
import com.elarbiallam.order.client.UserClient;
import com.elarbiallam.order.dto.*;
import com.elarbiallam.order.entity.Order;
import com.elarbiallam.order.entity.OrderLine;
import com.elarbiallam.order.enums.OrderStatus;
import com.elarbiallam.order.exception.BusinessException;
import com.elarbiallam.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartClient cartClient;
    private final UserClient userClient;

    @Transactional
    public Long createOrder(String userId, OrderRequest request) {
        CartResponse cart = cartClient.getMyCart();

        if (cart == null || cart.items().isEmpty()) {
            throw new BusinessException("Impossible de passer commande : Le panier est vide.");
        }

        Order order = Order.builder()
                .reference("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .userId(userId)
                .totalAmount(cart.totalAmount())
                .paymentMethod(request.paymentMethod())
                .status(OrderStatus.PENDING)
                .build();

        List<OrderLine> orderLines = cart.items().stream()
                .map(cartItem -> OrderLine.builder()
                        .order(order)
                        .productId(cartItem.productId())
                        .productName(cartItem.productName())
                        .quantity(cartItem.quantity())
                        .price(cartItem.unitPrice())
                        .build())
                .collect(Collectors.toList());

        order.setOrderLines(orderLines);
        Order savedOrder = orderRepository.save(order);
        cartClient.clearCart();

        log.info("Commande créée avec succès : ID {}", savedOrder.getId());
        return savedOrder.getId();
    }

    // CLIENT : Voir ses commandes (Pas besoin d'email, il sait qui il est)
    public List<OrderResponse> getMyOrders(String userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // CLIENT : Voir une commande (Pas besoin d'email)
    public OrderResponse getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new BusinessException("Commande non trouvée"));
    }

    // ADMIN : Voir tout (Besoin de l'email pour savoir qui a commandé)
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        Page<Order> ordersPage = orderRepository.findAll(pageable);

        List<OrderResponse> responseList = ordersPage.getContent().stream()
                .map(this::mapToResponseWithUserCheck) // Appel spécial avec info User
                .collect(Collectors.toList());

        return new PageImpl<>(responseList, pageable, ordersPage.getTotalElements());
    }

    // --- MAPPERS ---

    // Mapper simple (Client) -> Email null
    private OrderResponse mapToResponse(Order order) {
        return mapToResponseInternal(order, null);
    }

    // Mapper enrichi (Admin) -> Tente de récupérer l'email
    private OrderResponse mapToResponseWithUserCheck(Order order) {
        String email = null;
        try {
            UserResponse user = userClient.getUserById(order.getUserId());
            if (user != null) {
                email = user.email();
            }
        } catch (Exception e) {
            log.warn("Impossible de récupérer l'email pour la commande {}", order.getId());
        }
        return mapToResponseInternal(order, email);
    }

    // Méthode interne commune
    private OrderResponse mapToResponseInternal(Order order, String email) {
        List<OrderLineResponse> lines = order.getOrderLines().stream()
                .map(line -> new OrderLineResponse(
                        line.getProductId(),
                        line.getProductName(),
                        line.getQuantity(),
                        line.getPrice()
                )).collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getReference(),
                order.getTotalAmount(),
                order.getPaymentMethod(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getUserId(), // L'Admin a l'ID s'il veut chercher plus d'infos
                email,             // L'Admin a l'email directement
                lines
        );
    }
}