package com.novelstudio.backend.model;

import java.util.List;

public record ChapterReviewResult(
    String chapterId,
    String chapterTitle,
    List<ReviewIssue> issues
) {}
