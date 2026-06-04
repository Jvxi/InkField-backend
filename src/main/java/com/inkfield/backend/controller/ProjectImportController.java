package com.inkfield.backend.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inkfield.backend.model.Project;
import com.inkfield.backend.model.ProjectImportAnalysis;
import com.inkfield.backend.service.ProjectImportService;
import com.inkfield.backend.service.ProjectNormalizer;
import com.inkfield.backend.service.ProjectStore;

@RestController
@RequestMapping("/api/project/import")
public class ProjectImportController {
    private final ProjectStore projectStore;
    private final ProjectNormalizer normalizer;
    private final ProjectImportService projectImportService;

    public ProjectImportController(
        ProjectStore projectStore,
        ProjectNormalizer normalizer,
        ProjectImportService projectImportService
    ) {
        this.projectStore = projectStore;
        this.normalizer = normalizer;
        this.projectImportService = projectImportService;
    }

    /** @deprecated 请使用 {@link #analyzeOutline} 与 {@link #analyzeChapters} 分拆接口 */
    @Deprecated
    @PostMapping("/analyze")
    public ProjectImportAnalysis analyze(@RequestBody(required = false) Map<String, String> payload) {
        Project project = normalizer.normalize(projectStore.loadActiveProject().project());
        String outlineText = payload == null ? "" : payload.getOrDefault("outlineText", "");
        String chaptersText = payload == null ? "" : payload.getOrDefault("chaptersText", "");
        return projectImportService.analyze(project, outlineText, chaptersText);
    }

    @PostMapping("/analyze/outline")
    public ProjectImportAnalysis analyzeOutline(@RequestBody(required = false) Map<String, String> payload) {
        Project project = normalizer.normalize(projectStore.loadActiveProject().project());
        String outlineText = payload == null ? "" : payload.getOrDefault("outlineText", "");
        return projectImportService.analyzeOutline(project, outlineText);
    }

    @PostMapping("/analyze/chapters")
    public ProjectImportAnalysis analyzeChapters(@RequestBody(required = false) Map<String, String> payload) {
        Project project = normalizer.normalize(projectStore.loadActiveProject().project());
        String chaptersText = payload == null ? "" : payload.getOrDefault("chaptersText", "");
        return projectImportService.analyzeChapters(project, chaptersText);
    }
}
