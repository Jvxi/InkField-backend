package com.inkfield.backend.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.inkfield.backend.model.AiSettings;
import com.inkfield.backend.model.Chapter;
import com.inkfield.backend.model.CharacterProfile;
import com.inkfield.backend.model.ForeshadowingItem;
import com.inkfield.backend.model.OnboardingAnswer;
import com.inkfield.backend.model.OnboardingQuestion;
import com.inkfield.backend.model.OnboardingState;
import com.inkfield.backend.model.OutlineNode;
import com.inkfield.backend.model.Project;
import com.inkfield.backend.model.ProjectMeta;

@Component
public class ProjectNormalizer {
    private final PublishPlatformCatalog publishPlatformCatalog;
    private final NovelTypeCatalog novelTypeCatalog;
    private final SystemPromptComposer systemPromptComposer;

    public ProjectNormalizer(
        PublishPlatformCatalog publishPlatformCatalog,
        NovelTypeCatalog novelTypeCatalog,
        SystemPromptComposer systemPromptComposer
    ) {
        this.publishPlatformCatalog = publishPlatformCatalog;
        this.novelTypeCatalog = novelTypeCatalog;
        this.systemPromptComposer = systemPromptComposer;
    }

    public Project normalize(Project source) {
        ProjectMeta meta = normalizeMeta(source == null ? null : source.meta());
        AiSettings aiSettings = normalizeAiSettings(source == null ? null : source.aiSettings());
        List<OutlineNode> outlineNodes = normalizeOutlineNodes(source == null ? null : source.outlineNodes());
        List<CharacterProfile> characters = normalizeCharacters(source == null ? null : source.characters());
        List<ForeshadowingItem> foreshadowing = normalizeForeshadowing(source == null ? null : source.foreshadowing());
        List<Chapter> chapters = normalizeChapters(source == null ? null : source.chapters());
        OnboardingState onboarding = normalizeOnboarding(source == null ? null : source.onboarding());
        String updatedAt = textOrDefault(source == null ? null : source.updatedAt(), Instant.now().toString());

        return new Project(meta, aiSettings, onboarding, outlineNodes, characters, foreshadowing, chapters, updatedAt);
    }

    public AiSettings normalizeAiSettingsOnly(AiSettings source) {
        return normalizeAiSettings(source);
    }

    public Project withUpdatedTimestamp(Project project) {
        return new Project(
            project.meta(),
            project.aiSettings(),
            project.onboarding(),
            project.outlineNodes(),
            project.characters(),
            project.foreshadowing(),
            project.chapters(),
            Instant.now().toString()
        );
    }

    private OnboardingState normalizeOnboarding(OnboardingState source) {
        if (source == null) {
            return new OnboardingState(false, List.of(), List.of());
        }

        List<OnboardingQuestion> questions = source.questions() == null
            ? List.of()
            : source.questions().stream()
                .filter(Objects::nonNull)
                .map(question -> new OnboardingQuestion(
                    text(question.id()),
                    text(question.title()),
                    text(question.hint()),
                    text(question.placeholder())
                ))
                .filter(question -> !question.id().isBlank() && !question.title().isBlank())
                .toList();

        List<OnboardingAnswer> answers = source.answers() == null
            ? List.of()
            : source.answers().stream()
                .filter(Objects::nonNull)
                .map(answer -> new OnboardingAnswer(
                    text(answer.questionId()),
                    text(answer.question()),
                    text(answer.answer())
                ))
                .filter(answer -> !answer.questionId().isBlank())
                .toList();

        boolean completed = source.completed() && questions.size() >= 15 && answers.size() >= 15;
        return new OnboardingState(completed, questions, answers);
    }

    private ProjectMeta normalizeMeta(ProjectMeta source) {
        if (source == null) {
            return new ProjectMeta(
                "",
                "",
                novelTypeCatalog.formatGenreLabel(DEFAULT_AUDIENCE, DEFAULT_TYPE),
                "",
                "",
                "",
                List.of(),
                List.of(),
                true,
                "qidian",
                DEFAULT_AUDIENCE,
                DEFAULT_TYPE
            );
        }

        String audienceChannel = text(source.audienceChannel());
        String novelType = text(source.novelType());
        if (audienceChannel.isBlank() && novelType.isBlank()) {
            audienceChannel = novelTypeCatalog.inferAudienceFromLegacyGenre(source.genre());
            novelType = novelTypeCatalog.inferTypeFromLegacyGenre(source.genre(), audienceChannel);
        }

        audienceChannel = novelTypeCatalog.normalizeAudience(audienceChannel);
        novelType = novelTypeCatalog.normalizeNovelType(audienceChannel, novelType);
        String genreLabel = novelTypeCatalog.formatGenreLabel(audienceChannel, novelType);

        return new ProjectMeta(
            text(source.title()),
            text(source.synopsis()),
            genreLabel,
            text(source.premise()),
            text(source.tone()),
            text(source.targetLength()),
            normalizeLines(source.styleRules()),
            normalizeLines(source.worldRules()),
            source.strictMode(),
            publishPlatformCatalog.normalizePlatformId(source.publishPlatform()),
            audienceChannel,
            novelType
        );
    }

    private static final String DEFAULT_AUDIENCE = "male";
    private static final String DEFAULT_TYPE = "xuanyi";

    private AiSettings normalizeAiSettings(AiSettings source) {
        if (source == null) {
            return new AiSettings(false, "openai-compatible", "", "", "", 0.7, 1800, 0, "");
        }

        double temperature = source.temperature();
        if (Double.isNaN(temperature) || temperature < 0.0) {
            temperature = 0.0;
        }
        if (temperature > 2.0) {
            temperature = 2.0;
        }

        int maxTokens = source.maxTokens();
        if (maxTokens < 128) {
            maxTokens = 128;
        }
        if (maxTokens > 8000) {
            maxTokens = 8000;
        }

        return new AiSettings(
            source.enabled(),
            textOrDefault(source.provider(), "openai-compatible"),
            text(source.baseUrl()),
            text(source.apiKey()),
            text(source.model()),
            temperature,
            maxTokens,
            source.contextWindowSize(),
            systemPromptComposer.normalizeCustomPrompt(text(source.systemPrompt()))
        );
    }

    private List<OutlineNode> normalizeOutlineNodes(List<OutlineNode> source) {
        List<OutlineNode> result = new ArrayList<>();
        List<OutlineNode> safeSource = source == null ? List.of() : source;
        int fallbackOrder = 1;

        for (OutlineNode node : safeSource) {
            if (node == null) {
                continue;
            }
            result.add(new OutlineNode(
                textOrDefault(node.id(), randomId()),
                node.order() <= 0 ? fallbackOrder : node.order(),
                text(node.title()),
                text(node.summary()),
                text(node.objective()),
                text(node.keyConflict()),
                normalizeLines(node.mustKeep()),
                normalizeLines(node.forbidden())
            ));
            fallbackOrder++;
        }
        return result;
    }

    private List<CharacterProfile> normalizeCharacters(List<CharacterProfile> source) {
        List<CharacterProfile> result = new ArrayList<>();
        List<CharacterProfile> safeSource = source == null ? List.of() : source;

        for (CharacterProfile character : safeSource) {
            if (character == null) {
                continue;
            }
            result.add(new CharacterProfile(
                textOrDefault(character.id(), randomId()),
                text(character.name()),
                text(character.role()),
                text(character.profile()),
                text(character.motivation()),
                text(character.constraint()),
                text(character.relationships())
            ));
        }
        return result;
    }

    private List<ForeshadowingItem> normalizeForeshadowing(List<ForeshadowingItem> source) {
        List<ForeshadowingItem> result = new ArrayList<>();
        List<ForeshadowingItem> safeSource = source == null ? List.of() : source;

        for (ForeshadowingItem item : safeSource) {
            if (item == null) {
                continue;
            }
            String status = text(item.status());
            if (!List.of("planned", "revealed", "paid_off").contains(status)) {
                status = "planned";
            }
            result.add(new ForeshadowingItem(
                textOrDefault(item.id(), randomId()),
                text(item.title()),
                text(item.setup()),
                text(item.payoff()),
                text(item.plannedReveal()),
                status,
                text(item.notes())
            ));
        }
        return result;
    }

    private List<Chapter> normalizeChapters(List<Chapter> source) {
        List<Chapter> result = new ArrayList<>();
        List<Chapter> safeSource = source == null ? List.of() : source;
        int fallbackOrder = 1;

        for (Chapter chapter : safeSource) {
            if (chapter == null) {
                continue;
            }
            result.add(new Chapter(
                textOrDefault(chapter.id(), randomId()),
                chapter.order() <= 0 ? fallbackOrder : chapter.order(),
                text(chapter.title()),
                text(chapter.summary()),
                text(chapter.purpose()),
                normalizeIdList(chapter.outlineNodeIds()),
                normalizeIdList(chapter.characterIds()),
                normalizeIdList(chapter.foreshadowingIds()),
                normalizeLines(chapter.mandatoryBeats()),
                normalizeLines(chapter.forbiddenContent()),
                text(chapter.notes()),
                text(chapter.draft())
            ));
            fallbackOrder++;
        }
        return result;
    }

    private List<String> normalizeLines(List<String> source) {
        if (source == null || source.isEmpty()) {
            return List.of();
        }
        return source.stream()
            .filter(Objects::nonNull)
            .map(this::text)
            .filter(value -> !value.isBlank())
            .toList();
    }

    private List<String> normalizeIdList(List<String> source) {
        if (source == null || source.isEmpty()) {
            return List.of();
        }
        return source.stream()
            .filter(Objects::nonNull)
            .map(this::text)
            .filter(value -> !value.isBlank())
            .distinct()
            .toList();
    }

    private String text(String value) {
        return value == null ? "" : value.trim();
    }

    private String textOrDefault(String value, String fallback) {
        String normalized = text(value);
        return normalized.isBlank() ? fallback : normalized;
    }

    private String randomId() {
        return UUID.randomUUID().toString();
    }
}
