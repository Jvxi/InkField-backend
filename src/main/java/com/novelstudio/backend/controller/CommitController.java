package com.novelstudio.backend.controller;

import com.novelstudio.backend.model.commit.ChapterCommit;
import com.novelstudio.backend.service.story.ChapterCommitService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/commit")
public class CommitController {

    private final ChapterCommitService chapterCommitService;

    public CommitController(ChapterCommitService chapterCommitService) {
        this.chapterCommitService = chapterCommitService;
    }

    /**
     * 提交章节
     */
    @PostMapping("/{bookId}/chapter/{chapter}")
    public ResponseEntity<ChapterCommit> commitChapter(
            @PathVariable String bookId,
            @PathVariable int chapter,
            @RequestBody Map<String, Object> commitData
    ) {
        ChapterCommit result = chapterCommitService.commitChapter(bookId, chapter, commitData);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取提交记录
     */
    @GetMapping("/{bookId}/chapter/{chapter}")
    public ResponseEntity<ChapterCommit> getCommit(
            @PathVariable String bookId,
            @PathVariable int chapter
    ) {
        return chapterCommitService.getCommit(bookId, chapter)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 获取所有提交记录
     */
    @GetMapping("/{bookId}/chapters")
    public ResponseEntity<List<ChapterCommit>> getAllCommits(@PathVariable String bookId) {
        List<ChapterCommit> commits = chapterCommitService.getAllCommits(bookId);
        return ResponseEntity.ok(commits);
    }
}
