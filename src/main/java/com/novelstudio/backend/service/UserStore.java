package com.novelstudio.backend.service;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.novelstudio.backend.model.UserAccount;
import com.novelstudio.backend.persistence.entity.UserEntity;
import com.novelstudio.backend.persistence.repository.UserRepository;

@Component
public class UserStore {
    private final UserRepository userRepository;

    public UserStore(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<UserAccount> findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email).map(this::toModel);
    }

    public Optional<UserAccount> findById(String userId) {
        return userRepository.findById(userId).map(this::toModel);
    }

    @Transactional
    public void save(UserAccount user) {
        userRepository.save(toEntity(user));
    }

    public boolean isEmpty() {
        return userRepository.count() == 0;
    }

    private UserAccount toModel(UserEntity entity) {
        return new UserAccount(
            entity.getId(),
            entity.getEmail(),
            entity.getPasswordHash(),
            entity.getSalt(),
            entity.getCreatedAt()
        );
    }

    private UserEntity toEntity(UserAccount user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.id());
        entity.setEmail(user.email());
        entity.setPasswordHash(user.passwordHash());
        entity.setSalt(user.salt());
        entity.setCreatedAt(user.createdAt());
        return entity;
    }
}
