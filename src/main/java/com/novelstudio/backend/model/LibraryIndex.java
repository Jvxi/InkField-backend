package com.novelstudio.backend.model;

import java.util.List;

public record LibraryIndex(
    String activeBookId,
    List<BookSummary> books
) {
}
