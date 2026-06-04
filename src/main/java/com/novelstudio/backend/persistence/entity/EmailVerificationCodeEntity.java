package com.novelstudio.backend.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "email_verification_codes")
public class EmailVerificationCodeEntity {
    @Id
    @Column(length = 255)
    private String email;

    @Column(nullable = false, length = 10)
    private String code;

    @Column(name = "expires_at", nullable = false)
    private long expiresAtEpochSecond;

    @Column(name = "last_sent_at", nullable = false)
    private long lastSentAtEpochSecond;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getExpiresAtEpochSecond() {
        return expiresAtEpochSecond;
    }

    public void setExpiresAtEpochSecond(long expiresAtEpochSecond) {
        this.expiresAtEpochSecond = expiresAtEpochSecond;
    }

    public long getLastSentAtEpochSecond() {
        return lastSentAtEpochSecond;
    }

    public void setLastSentAtEpochSecond(long lastSentAtEpochSecond) {
        this.lastSentAtEpochSecond = lastSentAtEpochSecond;
    }
}
