package com.novelstudio.backend.persistence.repository;

import com.novelstudio.backend.persistence.entity.StoryMasterSettingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoryMasterSettingRepository extends JpaRepository<StoryMasterSettingEntity, String> {

    Optional<StoryMasterSettingEntity> findByBookId(String bookId);

    void deleteByBookId(String bookId);
}
