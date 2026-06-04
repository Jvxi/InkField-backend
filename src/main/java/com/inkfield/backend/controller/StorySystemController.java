package com.inkfield.backend.controller;

import com.inkfield.backend.model.story.*;
import com.inkfield.backend.service.story.StorySystemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/story-system")
public class StorySystemController {

    private final StorySystemService storySystemService;

    public StorySystemController(StorySystemService storySystemService) {
        this.storySystemService = storySystemService;
    }

    /**
     * 题材路由
     */
    @PostMapping("/{bookId}/route")
    public ResponseEntity<Map<String, Object>> routeGenre(
            @PathVariable String bookId,
            @RequestBody Map<String, String> request
    ) {
        String query = request.getOrDefault("query", "");
        String genre = request.get("genre");
        Map<String, Object> result = storySystemService.routeGenre(bookId, query, genre);
        return ResponseEntity.ok(result);
    }

    /**
     * 生成主设定合同
     */
    @PostMapping("/{bookId}/master-setting")
    public ResponseEntity<MasterSetting> generateMasterSetting(
            @PathVariable String bookId,
            @RequestBody Map<String, String> request
    ) {
        String query = request.getOrDefault("query", "");
        String genre = request.get("genre");
        MasterSetting result = storySystemService.generateMasterSetting(bookId, query, genre);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取主设定合同
     */
    @GetMapping("/{bookId}/master-setting")
    public ResponseEntity<MasterSetting> getMasterSetting(@PathVariable String bookId) {
        return storySystemService.getMasterSetting(bookId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 生成卷级合同
     */
    @PostMapping("/{bookId}/volume/{volume}/brief")
    public ResponseEntity<VolumeBrief> generateVolumeBrief(
            @PathVariable String bookId,
            @PathVariable int volume
    ) {
        VolumeBrief result = storySystemService.generateVolumeBrief(bookId, volume);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取卷级合同
     */
    @GetMapping("/{bookId}/volume/{volume}/brief")
    public ResponseEntity<VolumeBrief> getVolumeBrief(
            @PathVariable String bookId,
            @PathVariable int volume
    ) {
        return storySystemService.getVolumeBrief(bookId, volume)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 生成章节级合同
     */
    @PostMapping("/{bookId}/chapter/{chapter}/brief")
    public ResponseEntity<ChapterBrief> generateChapterBrief(
            @PathVariable String bookId,
            @PathVariable int chapter,
            @RequestBody(required = false) Map<String, Object> chapterDirective
    ) {
        ChapterBrief result = storySystemService.generateChapterBrief(bookId, chapter, chapterDirective);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取章节级合同
     */
    @GetMapping("/{bookId}/chapter/{chapter}/brief")
    public ResponseEntity<ChapterBrief> getChapterBrief(
            @PathVariable String bookId,
            @PathVariable int chapter
    ) {
        return storySystemService.getChapterBrief(bookId, chapter)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 生成审查合同
     */
    @PostMapping("/{bookId}/chapter/{chapter}/review-contract")
    public ResponseEntity<ReviewContract> generateReviewContract(
            @PathVariable String bookId,
            @PathVariable int chapter
    ) {
        ReviewContract result = storySystemService.generateReviewContract(bookId, chapter);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取审查合同
     */
    @GetMapping("/{bookId}/chapter/{chapter}/review-contract")
    public ResponseEntity<ReviewContract> getReviewContract(
            @PathVariable String bookId,
            @PathVariable int chapter
    ) {
        return storySystemService.getReviewContract(bookId, chapter)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
