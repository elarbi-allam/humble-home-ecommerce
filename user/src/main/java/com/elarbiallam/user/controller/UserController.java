package com.elarbiallam.user.controller;

import com.elarbiallam.user.dto.UserRegistrationRequest;
import com.elarbiallam.user.dto.UserResponse;
import com.elarbiallam.user.dto.UserUpdateRequest;
import com.elarbiallam.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 1. Inscription (Public) - Admin créé inactif, Client créé actif
    @PostMapping
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationRequest request) {
        String userId = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userId);
    }

    // 2. Récupérer son propre profil (Nécessite token)
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile(Principal principal) {
        // Principal.getName() retourne le Keycloak ID (sub) grâce au Token JWT
        return ResponseEntity.ok(userService.getUser(principal.getName()));
    }

    // 3. Modifier son propre profil
    @PutMapping("/me")
    public ResponseEntity<Void> updateMyProfile(Principal principal, @RequestBody UserUpdateRequest request) {
        userService.updateProfile(principal.getName(), request);
        return ResponseEntity.noContent().build();
    }

    // 4. ADMIN - Liste paginée des utilisateurs
    // Ex: GET /api/users?page=0&size=10
    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(userService.getAllUsers(PageRequest.of(page, size)));
    }

    // 5. ADMIN - Valider un compte (Admin en attente)
    @PatchMapping("/{id}/validate")
    public ResponseEntity<Void> validateUser(@PathVariable String id) {
        userService.updateUserStatus(id, true);
        return ResponseEntity.noContent().build();
    }

    // 6. ADMIN - Suspendre un compte (Client ou Admin)
    @PatchMapping("/{id}/suspend")
    public ResponseEntity<Void> suspendUser(@PathVariable String id) {
        userService.updateUserStatus(id, false);
        return ResponseEntity.noContent().build();
    }

    // 7. ADMIN - Voir un user spécifique
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUser(id));
    }
}