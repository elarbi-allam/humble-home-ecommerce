package com.elarbiallam.order.client;

import com.elarbiallam.order.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service") // Nom exact dans Eureka
public interface UserClient {

    // Assure-toi que ton User Service a bien un endpoint GET /api/users/{id}
    // Si ton User Service utilise Keycloak ID (String), garde String id.
    @GetMapping("/api/users/{id}")
    UserResponse getUserById(@PathVariable("id") String id);
}