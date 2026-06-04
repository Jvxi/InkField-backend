package com.novelstudio.backend.model;

public record OnboardingQuestion(
    String id,
    String title,
    String hint,
    String placeholder
) {
}
