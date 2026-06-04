package com.inkfield.backend.model;

import java.util.List;

public record Chapter(
    String id,
    int order,
    String title,
    String summary,
    String purpose,
    List<String> outlineNodeIds,
    List<String> characterIds,
    List<String> foreshadowingIds,
    List<String> mandatoryBeats,
    List<String> forbiddenContent,
    String notes,
    String draft
) {
}
