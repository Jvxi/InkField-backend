package com.novelstudio.backend.service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.novelstudio.backend.config.NovelAppProperties;
import com.novelstudio.backend.config.NovelMailProperties;
import com.novelstudio.backend.service.mail.RegistrationVerificationEmail;

import jakarta.mail.internet.MimeMessage;
import com.novelstudio.backend.exception.ApiException;
import com.novelstudio.backend.persistence.entity.EmailVerificationCodeEntity;
import com.novelstudio.backend.persistence.repository.EmailVerificationCodeRepository;

@Component
public class EmailCodeService {
    private static final Logger log = LoggerFactory.getLogger(EmailCodeService.class);
    private static final long TTL_SECONDS = 180;
    private static final long RESEND_COOLDOWN_SECONDS = 60;

    private final SecureRandom random = new SecureRandom();
    private final EmailVerificationCodeRepository codeRepository;
    private final JavaMailSender mailSender;
    private final NovelMailProperties mailProperties;
    private final NovelAppProperties appProperties;
    private final boolean mailEnabled;

    public EmailCodeService(
        EmailVerificationCodeRepository codeRepository,
        JavaMailSender mailSender,
        NovelMailProperties mailProperties,
        NovelAppProperties appProperties,
        @Value("${spring.mail.host:}") String mailHost
    ) {
        this.codeRepository = codeRepository;
        this.mailSender = mailSender;
        this.mailProperties = mailProperties;
        this.appProperties = appProperties;
        this.mailEnabled = mailHost != null && !mailHost.isBlank();
    }

    @Transactional
    public String sendRegisterCode(String email) {
        String normalizedEmail = normalizeEmail(email);
        long now = Instant.now().getEpochSecond();
        EmailVerificationCodeEntity existing = codeRepository.findById(normalizedEmail).orElse(null);
        if (existing != null && now - existing.getLastSentAtEpochSecond() < RESEND_COOLDOWN_SECONDS) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "发送过于频繁，请稍后再试。");
        }

        if (!mailEnabled) {
            if (mailProperties.isRequired()) {
                throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "邮件服务未配置，无法发送验证码。");
            }
        }

        String code = String.format("%06d", random.nextInt(1_000_000));
        EmailVerificationCodeEntity entity = new EmailVerificationCodeEntity();
        entity.setEmail(normalizedEmail);
        entity.setCode(code);
        entity.setExpiresAtEpochSecond(now + TTL_SECONDS);
        entity.setLastSentAtEpochSecond(now);
        codeRepository.save(entity);

        if (mailEnabled) {
            try {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                helper.setFrom(mailProperties.getFrom(), RegistrationVerificationEmail.APP_NAME);
                helper.setTo(normalizedEmail);
                helper.setSubject(RegistrationVerificationEmail.SUBJECT);
                helper.setText(
                    RegistrationVerificationEmail.plainText(code),
                    RegistrationVerificationEmail.html(code, appProperties.registerUrl())
                );
                mailSender.send(mimeMessage);
            } catch (Exception exception) {
                codeRepository.deleteById(normalizedEmail);
                log.error("SMTP 发送失败: {}", exception.getMessage());
                throw new ApiException(HttpStatus.BAD_GATEWAY, "邮件发送失败，请检查 SMTP 配置（主机、端口、账号、授权码）。");
            }
        } else {
            log.info("[开发模式] 注册验证码 {} -> {}", normalizedEmail, code);
        }
        return code;
    }

    @Transactional
    public void verifyRegisterCode(String email, String code) {
        String normalizedEmail = normalizeEmail(email);
        EmailVerificationCodeEntity entry = codeRepository.findById(normalizedEmail)
            .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST, "请先获取邮箱验证码。"));
        codeRepository.deleteById(normalizedEmail);
        if (Instant.now().getEpochSecond() > entry.getExpiresAtEpochSecond()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "邮箱验证码已过期，请重新获取。");
        }
        if (!entry.getCode().equals(code == null ? "" : code.trim())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "邮箱验证码错误。");
        }
    }

    public boolean isDevModeWithoutMail() {
        return !mailEnabled;
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "邮箱格式不正确。");
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
