package com.novelstudio.backend.persistence.repository;

import com.novelstudio.backend.persistence.entity.StoryEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoryEventRepository extends JpaRepository<StoryEventEntity, String> {

    List<StoryEventEntity> findByBookId(String bookId);

    List<StoryEventEntity> findByBookIdAndChapterNumber(String bookId, Integer chapterNumber);

    List<StoryEventEntity> findByBookIdAndSubject(String bookId, String subject);

    List<StoryEventEntity> findByBookIdAndEventType(String bookId, StoryEventEntity.EventType eventType);

    void deleteByBookId(String bookId);
}
