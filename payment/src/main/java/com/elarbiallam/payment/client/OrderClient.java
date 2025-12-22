package com.elarbiallam.payment.client;

import com.elarbiallam.payment.dto.OrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "order-service", url = "${application.config.order-url}")
public interface OrderClient {

    @GetMapping("/api/orders/{orderId}")
    OrderResponse getOrderById(@PathVariable("orderId") Long orderId);

    @PutMapping("/api/orders/{orderId}/pay")
    void updateOrderStatus(@PathVariable("orderId") Long orderId);
}