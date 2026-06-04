package com.novelstudio.backend.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.novelstudio.backend.persistence.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByEmailIgnoreCase(String email);
}
