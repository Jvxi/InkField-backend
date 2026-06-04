package com.inkfield.backend.model;

public record ImportedChapter(
    int order,
    String title,
    String summary,
    String purpose,
    String content
) {
}
