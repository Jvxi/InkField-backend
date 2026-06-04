package com.inkfield.backend.service.story;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inkfield.backend.model.commit.ChapterCommit;
import com.inkfield.backend.persistence.entity.ChapterCommitEntity;
import com.inkfield.backend.persistence.repository.ChapterCommitRepository;
import com.inkfield.backend.service.memory.MemoryService;
import com.inkfield.backend.service.rag.RagService;
import com.inkfield.backend.service.readingpower.ReadingPowerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

@Service
public class ChapterCommitService {

    private static final Logger log = LoggerFactory.getLogger(ChapterCommitService.class);

    private final ChapterCommitRepository chapterCommitRepository;
    private final MemoryService memoryService;
    private final RagService ragService;
    private final ReadingPowerService readingPowerService;
    private final ObjectMapper objectMapper;

    public ChapterCommitService(
            ChapterCommitRepository chapterCommitRepository,
            MemoryService memoryService,
            RagService ragService,
            ReadingPowerService readingPowerService,
            ObjectMapper objectMapper
    ) {
        this.chapterCommitRepository = chapterCommitRepository;
        this.memoryService = memoryService;
        this.ragService = ragService;
        this.readingPowerService = readingPowerService;
        this.objectMapper = objectMapper;
    }

    /**
     * 提交章节
     */
    @SuppressWarnings("unchecked")
    @Transactional
    public ChapterCommit commitChapter(String bookId, int chapterNumber, Map<String, Object> commitData) {
        // 检查是否已提交
        Optional<ChapterCommitEntity> existing = chapterCommitRepository.findByBookIdAndChapterNumber(bookId, chapterNumber);
        if (existing.isPresent()) {
            log.warn("章节已提交: bookId={}, chapter={}", bookId, chapterNumber);
            return toModel(existing.get());
        }

        // 1. 构建提交记录
        ChapterCommitEntity entity = new ChapterCommitEntity();
        entity.setBookId(bookId);
        entity.setChapterNumber(chapterNumber);
        entity.setStatus(ChapterCommitEntity.CommitStatus.accepted);
        entity.setContractRefsJson(serialize(commitData.get("contract_refs")));
        entity.setOutlineSnapshotJson(serialize(commitData.get("outline_snapshot")));
        entity.setReviewResultJson(serialize(commitData.get("review_result")));
        entity.setFulfillmentResultJson(serialize(commitData.get("fulfillment_result")));
        entity.setDisambiguationResultJson(serialize(commitData.get("disambiguation_result")));
        entity.setSummaryText((String) commitData.getOrDefault("summary_text", ""));
        entity.setDominantStrand((String) commitData.getOrDefault("dominant_strand", ""));

        // 2. 执行投影
        Map<String, String> projectionStatus = new HashMap<>();
        projectionStatus.put("state", "done");
        projectionStatus.put("index", "done");
        projectionStatus.put("summary", "done");
        projectionStatus.put("memory", "done");
        projectionStatus.put("vector", "done");

        // 3. 记忆投影
        try {
            Map<String, Object> extractionResult = (Map<String, Object>) commitData.getOrDefault("extraction_result", Map.of());
            memoryService.updateFromChapterResult(bookId, chapterNumber, extractionResult);
        } catch (Exception e) {
            log.error("记忆投影失败", e);
            projectionStatus.put("memory", "failed");
        }

        // 4. 向量投影
        try {
            String chapterText = (String) commitData.getOrDefault("chapter_text", "");
            if (!chapterText.isEmpty()) {
                ragService.indexChapter(bookId, chapterNumber, chapterText);
            }
        } catch (Exception e) {
            log.error("向量投影失败", e);
            projectionStatus.put("vector", "failed");
        }

        // 5. 追读力分析
        try {
            String chapterText = (String) commitData.getOrDefault("chapter_text", "");
            if (!chapterText.isEmpty()) {
                readingPowerService.analyzeChapter(bookId, chapterNumber, chapterText);
            }
        } catch (Exception e) {
            log.error("追读力分析失败", e);
        }

        entity.setProjectionStatusJson(serialize(projectionStatus));
        chapterCommitRepository.save(entity);

        log.info("章节提交成功: bookId={}, chapter={}, status={}", bookId, chapterNumber, entity.getStatus());
        return toModel(entity);
    }

    /**
     * 获取提交记录
     */
    public Optional<ChapterCommit> getCommit(String bookId, int chapterNumber) {
        return chapterCommitRepository.findByBookIdAndChapterNumber(bookId, chapterNumber)
                .map(this::toModel);
    }

    /**
     * 获取所有提交记录
     */
    public List<ChapterCommit> getAllCommits(String bookId) {
        return chapterCommitRepository.findByBookId(bookId)
                .stream()
                .map(this::toModel)
                .toList();
    }

    // ============ 私有辅助方法 ============

    private ChapterCommit toModel(ChapterCommitEntity entity) {
        try {
            Map<String, Object> contractRefs = deserialize(entity.getContractRefsJson());
            Map<String, Object> outlineSnapshot = deserialize(entity.getOutlineSnapshotJson());
            Map<String, Object> reviewResult = deserialize(entity.getReviewResultJson());
            Map<String, Object> fulfillmentResult = deserialize(entity.getFulfillmentResultJson());
            Map<String, Object> disambiguationResult = deserialize(entity.getDisambiguationResultJson());
            Map<String, String> projectionStatus = deserializeStringMap(entity.getProjectionStatusJson());

            return new ChapterCommit(
                    entity.getId(),
                    entity.getChapterNumber(),
                    entity.getStatus().name(),
                    contractRefs,
                    outlineSnapshot,
                    reviewResult,
                    fulfillmentResult,
                    disambiguationResult,
                    entity.getSummaryText(),
                    entity.getDominantStrand(),
                    projectionStatus
            );
        } catch (Exception e) {
            log.error("反序列化ChapterCommit失败", e);
            return new ChapterCommit(entity.getChapterNumber());
        }
    }

    private Map<String, Object> deserialize(String json) {
        try {
            if (json == null || json.isEmpty()) {
                return Map.of();
            }
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (IOException e) {
            log.error("JSON反序列化失败", e);
            return Map.of();
        }
    }

    private Map<String, String> deserializeStringMap(String json) {
        try {
            if (json == null || json.isEmpty()) {
                return Map.of();
            }
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (IOException e) {
            log.error("JSON反序列化失败", e);
            return Map.of();
        }
    }

    private String serialize(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.error("JSON序列化失败", e);
            return "{}";
        }
    }
}
