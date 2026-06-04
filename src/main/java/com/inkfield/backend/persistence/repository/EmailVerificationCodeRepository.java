package com.inkfield.backend.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inkfield.backend.persistence.entity.EmailVerificationCodeEntity;

public interface EmailVerificationCodeRepository extends JpaRepository<EmailVerificationCodeEntity, String> {
}
