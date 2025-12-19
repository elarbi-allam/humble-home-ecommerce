package com.elarbiallam.user.service;

import com.elarbiallam.user.dto.UserRegistrationRequest;
import com.elarbiallam.user.dto.UserResponse;
import com.elarbiallam.user.dto.UserUpdateRequest;
import com.elarbiallam.user.mapper.UserMapper;
import com.elarbiallam.user.model.User;
import com.elarbiallam.user.repository.UserRepository;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final Keycloak keycloak;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Value("${app.keycloak.realm}")
    private String realm;

    @Transactional
    public String createUser(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email déjà utilisé");
        }

        // 1. Logique d'activation selon le rôle
        // Si ADMIN -> inactif (attente validation). Si CLIENT -> actif.
        String roleToAssign = request.role() != null ? request.role().toUpperCase() : "CLIENT";
        boolean isEnabled = "CLIENT".equals(roleToAssign);

        // 2. Préparer Keycloak User
        UserRepresentation userParam = new UserRepresentation();
        userParam.setUsername(request.email());
        userParam.setEmail(request.email());
        userParam.setFirstName(request.firstname());
        userParam.setLastName(request.lastname());
        userParam.setEnabled(isEnabled); // <--- C'est ici que Keycloak bloque ou autorise le login
        userParam.setEmailVerified(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.password());
        credential.setTemporary(false);
        userParam.setCredentials(List.of(credential));

        // 3. Création Keycloak
        UsersResource usersResource = keycloak.realm(realm).users();
        Response response = usersResource.create(userParam);

        if (response.getStatus() != 201) {
            log.error("Keycloak Error: {}", response.getStatus());
            throw new RuntimeException("Erreur création Keycloak");
        }

        String userId = CreatedResponseUtil.getCreatedId(response);

        // 4. Assigner le rôle Keycloak
        RoleRepresentation roleRep = keycloak.realm(realm).roles().get(roleToAssign).toRepresentation();
        usersResource.get(userId).roles().realmLevel().add(Collections.singletonList(roleRep));

        // 5. Sauvegarde DB Locale
        User appUser = User.builder()
                .firstname(request.firstname())
                .lastname(request.lastname())
                .email(request.email())
                .keycloakId(userId)
                .role(roleToAssign)
                .active(isEnabled) // Sync avec Keycloak
                .build();

        userRepository.save(appUser);
        log.info("User created: {} | Role: {} | Active: {}", request.email(), roleToAssign, isEnabled);

        return userId;
    }

    // Récupérer un user (Moi ou via ID pour admin)
    public UserResponse getUser(String keycloakId) {
        return userRepository.findByKeycloakId(keycloakId)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    // Liste paginée pour Admin
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toResponse);
    }

    // Modifier son propre profil
    @Transactional
    public void updateProfile(String keycloakId, UserUpdateRequest request) {
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstname(request.firstname());
        user.setLastname(request.lastname());
        userRepository.save(user);

        // Sync Keycloak (Optionnel mais recommandé pour garder la cohérence)
        UserResource userResource = keycloak.realm(realm).users().get(keycloakId);
        UserRepresentation kUser = userResource.toRepresentation();
        kUser.setFirstName(request.firstname());
        kUser.setLastName(request.lastname());
        userResource.update(kUser);
    }

    // Action Admin: Valider ou Suspendre
    @Transactional
    public void updateUserStatus(String id, boolean isActive) {
        User user = userRepository.findByKeycloakId(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 1. Update DB Locale
        user.setActive(isActive);
        userRepository.save(user);

        // 2. Update Keycloak (Bloque ou débloque le login réel)
        UserResource userResource = keycloak.realm(realm).users().get(id);
        UserRepresentation kUser = userResource.toRepresentation();
        kUser.setEnabled(isActive);
        userResource.update(kUser);

        log.info("User {} status changed to {}", id, isActive);
    }
}