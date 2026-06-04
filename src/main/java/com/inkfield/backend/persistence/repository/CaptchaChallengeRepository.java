package com.inkfield.backend.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.inkfield.backend.persistence.entity.CaptchaChallengeEntity;

public interface CaptchaChallengeRepository extends JpaRepository<CaptchaChallengeEntity, String> {
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("DELETE FROM CaptchaChallengeEntity c WHERE c.expiresAtEpochSecond < :now")
    int deleteExpired(@Param("now") long nowEpochSecond);
}
