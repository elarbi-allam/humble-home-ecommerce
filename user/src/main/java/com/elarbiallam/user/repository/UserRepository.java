package com.elarbiallam.user.repository;

import com.elarbiallam.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByKeycloakId(String keycloakId);
    boolean existsByEmail(String email);

    // Pagination native
    Page<User> findAll(Pageable pageable);
}