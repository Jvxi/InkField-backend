package com.novelstudio.backend.model;

public record ImportedCharacter(
    String name,
    String role,
    String profile,
    String motivation,
    String constraint,
    String relationships
) {
}
