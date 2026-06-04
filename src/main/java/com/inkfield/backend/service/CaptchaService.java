package com.inkfield.backend.service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.inkfield.backend.exception.ApiException;
import com.inkfield.backend.persistence.entity.CaptchaChallengeEntity;
import com.inkfield.backend.persistence.repository.CaptchaChallengeRepository;

@Component
public class CaptchaService {
    private static final long TTL_SECONDS = 300;
    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int CODE_LENGTH = 4;

    private final SecureRandom random = new SecureRandom();
    private final CaptchaChallengeRepository captchaRepository;

    public CaptchaService(CaptchaChallengeRepository captchaRepository) {
        this.captchaRepository = captchaRepository;
    }

    @Transactional
    public CaptchaChallenge createChallenge() {
        purgeExpired();
        String code = randomCode();
        String id = UUID.randomUUID().toString();

        CaptchaChallengeEntity entity = new CaptchaChallengeEntity();
        entity.setId(id);
        entity.setAnswer(code);
        entity.setExpiresAtEpochSecond(Instant.now().getEpochSecond() + TTL_SECONDS);
        captchaRepository.save(entity);
        return new CaptchaChallenge(id, code);
    }

    @Transactional(readOnly = true)
    public boolean matches(String captchaId, String answer) {
        if (captchaId == null || captchaId.isBlank()) {
            return false;
        }
        return captchaRepository.findById(captchaId)
            .map(entry -> !isExpired(entry) && normalize(answer).equals(normalize(entry.getAnswer())))
            .orElse(false);
    }

    @Transactional
    public void validate(String captchaId, String answer) {
        if (captchaId == null || captchaId.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "请先完成人机验证。");
        }
        CaptchaChallengeEntity entry = captchaRepository.findById(captchaId)
            .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "人机验证码已过期，请刷新后重试。"));
        captchaRepository.deleteById(captchaId);
        if (isExpired(entry)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "人机验证码已过期，请刷新后重试。");
        }
        if (!normalize(answer).equals(normalize(entry.getAnswer()))) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "人机验证码错误。");
        }
    }

    private String randomCode() {
        StringBuilder builder = new StringBuilder(CODE_LENGTH);
        for (int index = 0; index < CODE_LENGTH; index++) {
            builder.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return builder.toString();
    }

    private boolean isExpired(CaptchaChallengeEntity entry) {
        return Instant.now().getEpochSecond() > entry.getExpiresAtEpochSecond();
    }

    private String normalize(String answer) {
        return answer == null ? "" : answer.trim().toUpperCase(Locale.ROOT);
    }

    private void purgeExpired() {
        captchaRepository.deleteExpired(Instant.now().getEpochSecond());
    }

    public record CaptchaChallenge(String captchaId, String question) {
    }
}
