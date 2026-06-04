package com.novelstudio.backend.model;

import java.util.List;

public record PublishPlatformInfo(
    String id,
    String label,
    String description,
    List<String> writingRules
) {
}
