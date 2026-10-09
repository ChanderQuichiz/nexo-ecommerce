package com.nexo.ecommerce.auth.infrastructure.persistence;

import com.nexo.ecommerce.auth.application.ports.UserRepository;
import com.nexo.ecommerce.auth.domain.exception.EmailAlreadyRegisteredException;
import com.nexo.ecommerce.auth.domain.model.EmailAddress;
import com.nexo.ecommerce.auth.domain.model.User;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UserPersistenceAdapter implements UserRepository {
    private final SpringDataUserRepository repository;

    public UserPersistenceAdapter(SpringDataUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean existsByEmail(EmailAddress email) {
        return repository.existsByEmail(email.value());
    }

    @Override
    public User save(User user) {
        try {
            return UserMapper.toDomain(repository.saveAndFlush(UserMapper.toEntity(user)));
        } catch (DataIntegrityViolationException exception) {
            Throwable cause = exception;
            while (cause != null) {
                if (cause instanceof ConstraintViolationException violation
                        && "uk_users_email".equalsIgnoreCase(violation.getConstraintName())) {
                    throw new EmailAlreadyRegisteredException();
                }
                cause = cause.getCause();
            }
            throw exception;
        }
    }

    @Override
    public Optional<User> findByEmail(EmailAddress email) {
        return repository.findByEmail(email.value()).map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return repository.findById(id).map(UserMapper::toDomain);
    }
}
