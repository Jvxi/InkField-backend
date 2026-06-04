package com.inkfield.backend.controller;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inkfield.backend.auth.UserContext;
import com.inkfield.backend.model.AiSettings;
import com.inkfield.backend.model.ChapterGenerationResponse;
import com.inkfield.backend.model.Project;
import com.inkfield.backend.model.ProjectEnvelope;
import com.inkfield.backend.model.NovelTypeCatalogResponse;
import com.inkfield.backend.model.PublishPlatformInfo;
import com.inkfield.backend.service.GenerationService;
import com.inkfield.backend.service.NovelTypeCatalog;
import com.inkfield.backend.service.ProjectNormalizer;
import com.inkfield.backend.service.ProjectStore;
import com.inkfield.backend.service.PublishPlatformCatalog;
import com.inkfield.backend.service.TokenUsageService;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api")
public class ProjectController {
    private final ProjectStore projectStore;
    private final ProjectNormalizer normalizer;
    private final GenerationService generationService;
    private final PublishPlatformCatalog publishPlatformCatalog;
    private final NovelTypeCatalog novelTypeCatalog;
    private final TokenUsageService tokenUsageService;

    public ProjectController(
        ProjectStore projectStore,
        ProjectNormalizer normalizer,
        GenerationService generationService,
        PublishPlatformCatalog publishPlatformCatalog,
        NovelTypeCatalog novelTypeCatalog,
        TokenUsageService tokenUsageService
    ) {
        this.projectStore = projectStore;
        this.normalizer = normalizer;
        this.generationService = generationService;
        this.publishPlatformCatalog = publishPlatformCatalog;
        this.novelTypeCatalog = novelTypeCatalog;
        this.tokenUsageService = tokenUsageService;
    }

    @GetMapping("/health")
    public Map<String, Boolean> health() {
        return Map.of("ok", true);
    }

    @GetMapping("/token-usage")
    public Map<String, Object> tokenUsage() {
        ProjectEnvelope envelope = projectStore.loadActiveProject();
        AiSettings settings = envelope.project().aiSettings();
        String model = settings.model();

        TokenUsageService.TokenUsageSnapshot snapshot = tokenUsageService.getUsageWithRemoteQuota(settings);

        return Map.of(
            "promptTokens", snapshot.promptTokens(),
            "completionTokens", snapshot.completionTokens(),
            "totalTokens", snapshot.totalTokens(),
            "maxTokens", snapshot.maxTokens(),
            "remainingTokens", snapshot.remainingTokens(),
            "usagePercent", Math.round(snapshot.usagePercent() * 100.0) / 100.0,
            "model", model == null ? "" : model
        );
    }

    @GetMapping("/project")
    public ProjectEnvelope getProject() {
        return projectStore.loadActiveProject();
    }

    @PutMapping("/project")
    public ProjectEnvelope saveProject(@RequestBody Project project) {
        Project normalized = normalizer.normalize(project);
        ProjectEnvelope saved = projectStore.saveActiveProject(normalized);
        return new ProjectEnvelope(saved.bookId(), normalizer.normalize(saved.project()));
    }

    @GetMapping("/platforms")
    public List<PublishPlatformInfo> listPlatforms() {
        return publishPlatformCatalog.listAll();
    }

    @GetMapping("/novel-types")
    public NovelTypeCatalogResponse listNovelTypes() {
        return novelTypeCatalog.catalog();
    }

    @PostMapping("/chapters/{chapterId}/generate")
    public ChapterGenerationResponse generateChapter(@PathVariable String chapterId) {
        return generationService.generateChapter(chapterId);
    }

    @PostMapping(value = "/chapters/review/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamReviewChapters() {
        SseEmitter emitter = new SseEmitter(600_000L);
        String userId = UserContext.requireUserId();
        CompletableFuture.runAsync(() -> {
            UserContext.setUserId(userId);
            try {
                generationService.streamReviewChapters(emitter);
            } finally {
                UserContext.clear();
            }
        });
        return emitter;
    }

    @PostMapping(value = "/chapters/{chapterId}/generate/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamGenerateChapter(
            @PathVariable String chapterId,
            @RequestParam(name = "continue", required = false, defaultValue = "false") boolean continueMode) {
        SseEmitter emitter = new SseEmitter(240_000L);
        String userId = UserContext.requireUserId();
        CompletableFuture.runAsync(() -> {
            UserContext.setUserId(userId);
            try {
                generationService.streamGenerateChapter(chapterId, emitter, continueMode);
            } finally {
                UserContext.clear();
            }
        });
        return emitter;
    }

    @PostMapping("/ai/test")
    public Map<String, Object> testAiConnection(@RequestBody AiSettings aiSettings) {
        return generationService.testConnection(aiSettings);
    }

    @GetMapping("/system-prompt/preview")
    public Map<String, String> previewSystemPrompt() {
        Project project = normalizer.normalize(projectStore.loadActiveProject().project());
        return Map.of("prompt", generationService.previewSystemPrompt(project));
    }
}
