package com.novelstudio.backend.persistence.repository;

import com.novelstudio.backend.persistence.entity.StoryVolumeBriefEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoryVolumeBriefRepository extends JpaRepository<StoryVolumeBriefEntity, String> {

    List<StoryVolumeBriefEntity> findByBookId(String bookId);

    Optional<StoryVolumeBriefEntity> findByBookIdAndVolumeNumber(String bookId, Integer volumeNumber);

    void deleteByBookId(String bookId);
}
