package com.novelstudio.backend.model;

import java.util.List;

public record NovelTypeCatalogResponse(
    List<NovelAudienceInfo> audiences,
    List<NovelTypeInfo> types
) {
}
