package com.novelstudio.backend.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.novelstudio.backend.persistence.entity.EmailVerificationCodeEntity;

public interface EmailVerificationCodeRepository extends JpaRepository<EmailVerificationCodeEntity, String> {
}
