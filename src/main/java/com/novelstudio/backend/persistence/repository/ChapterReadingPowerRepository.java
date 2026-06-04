package com.novelstudio.backend.persistence.repository;

import com.novelstudio.backend.persistence.entity.ChapterReadingPowerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterReadingPowerRepository extends JpaRepository<ChapterReadingPowerEntity, String> {

    List<ChapterReadingPowerEntity> findByBookId(String bookId);

    Optional<ChapterReadingPowerEntity> findByBookIdAndChapterNumber(String bookId, Integer chapterNumber);

    void deleteByBookId(String bookId);
}
