package com.elarbiallam.user.mapper;

import com.elarbiallam.user.dto.UserResponse;
import com.elarbiallam.user.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getKeycloakId(),
                user.getFirstname(),
                user.getLastname(),
                user.getEmail(),
                user.getRole(),
                user.isActive(),
                user.getCreatedAt()
        );
    }
}