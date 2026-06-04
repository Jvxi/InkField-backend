package com.novelstudio.backend.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "auth_sessions")
public class AuthSessionEntity {
    @Id
    @Column(length = 128)
    private String token;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(name = "expires_at", nullable = false)
    private long expiresAtEpochSecond;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getExpiresAtEpochSecond() {
        return expiresAtEpochSecond;
    }

    public void setExpiresAtEpochSecond(long expiresAtEpochSecond) {
        this.expiresAtEpochSecond = expiresAtEpochSecond;
    }
}
