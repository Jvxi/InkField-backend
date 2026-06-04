package com.inkfield.backend.model;

import java.util.List;

public record NovelTypeInfo(
    String id,
    String label,
    String audienceChannel,
    String description,
    List<String> writingHints
) {
}
