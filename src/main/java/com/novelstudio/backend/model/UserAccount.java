package com.novelstudio.backend.model;

public record UserAccount(
    String id,
    String email,
    String passwordHash,
    String salt,
    String createdAt
) {
}
