package com.inkfield.backend.model;

public record ImportedChapterPreview(
    int order,
    String title,
    String summary,
    String draft
) {
}
