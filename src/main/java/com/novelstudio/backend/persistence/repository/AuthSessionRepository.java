package com.novelstudio.backend.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.novelstudio.backend.persistence.entity.AuthSessionEntity;

public interface AuthSessionRepository extends JpaRepository<AuthSessionEntity, String> {
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("DELETE FROM AuthSessionEntity s WHERE s.expiresAtEpochSecond < :now")
    int deleteExpired(@Param("now") long nowEpochSecond);
}
