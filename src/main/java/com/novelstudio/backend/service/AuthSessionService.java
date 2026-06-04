package com.novelstudio.backend.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.novelstudio.backend.exception.ApiException;
import com.novelstudio.backend.persistence.entity.AuthSessionEntity;
import com.novelstudio.backend.persistence.repository.AuthSessionRepository;

@Service
public class AuthSessionService {
    private final AuthSessionRepository authSessionRepository;

    public AuthSessionService(AuthSessionRepository authSessionRepository) {
        this.authSessionRepository = authSessionRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public String createSession(String userId, long ttlSeconds) {
        purgeExpired();
        String token = UUID.randomUUID().toString().replace("-", "") + UUID.randomUUID().toString().replace("-", "");
        AuthSessionEntity session = new AuthSessionEntity();
        session.setToken(token);
        session.setUserId(userId);
        session.setExpiresAtEpochSecond(Instant.now().getEpochSecond() + ttlSeconds);
        authSessionRepository.saveAndFlush(session);
        return token;
    }

    @Transactional
    public AuthSessionEntity requireSession(String token) {
        if (token == null || token.isBlank()) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "请先登录。");
        }
        purgeExpired();
        AuthSessionEntity session = authSessionRepository.findById(token.trim())
            .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "登录已失效，请重新登录。"));
        if (Instant.now().getEpochSecond() > session.getExpiresAtEpochSecond()) {
            authSessionRepository.deleteById(token.trim());
            throw new ApiException(HttpStatus.UNAUTHORIZED, "登录已过期，请重新登录。");
        }
        return session;
    }

    @Transactional
    public void revokeSession(String token) {
        if (token != null && !token.isBlank()) {
            authSessionRepository.deleteById(token.trim());
        }
    }

    private void purgeExpired() {
        authSessionRepository.deleteExpired(Instant.now().getEpochSecond());
    }
}
