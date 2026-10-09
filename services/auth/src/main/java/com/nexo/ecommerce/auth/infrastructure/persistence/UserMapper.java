package com.nexo.ecommerce.auth.infrastructure.persistence;

import com.nexo.ecommerce.auth.domain.model.EmailAddress;
import com.nexo.ecommerce.auth.domain.model.User;
import com.nexo.ecommerce.auth.domain.model.UserRole;

final class UserMapper {
    private UserMapper() {
    }

    static UserJpaEntity toEntity(User user) {
        return new UserJpaEntity(
                user.id(),
                user.email().value(),
                user.name(),
                user.passwordHash(),
                user.role().name(),
                user.createdAt());
    }

    static User toDomain(UserJpaEntity entity) {
        return new User(
                entity.getId(),
                new EmailAddress(entity.getEmail()),
                entity.getName(),
                entity.getPasswordHash(),
                UserRole.valueOf(entity.getRole()),
                entity.getCreatedAt());
    }
}
