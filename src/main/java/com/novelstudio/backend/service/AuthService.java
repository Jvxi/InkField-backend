package com.novelstudio.backend.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.novelstudio.backend.exception.ApiException;
import com.novelstudio.backend.model.UserAccount;

@Component
public class AuthService {
    private final UserStore userStore;
    private final BookLibraryService bookLibraryService;
    private final CaptchaService captchaService;
    private final EmailCodeService emailCodeService;
    private final AuthSessionService authSessionService;
    private final SecureRandom random = new SecureRandom();
    private final long tokenTtlSeconds;

    public AuthService(
        UserStore userStore,
        BookLibraryService bookLibraryService,
        CaptchaService captchaService,
        EmailCodeService emailCodeService,
        AuthSessionService authSessionService,
        @Value("${novel.auth.token-ttl-hours:168}") long tokenTtlHours
    ) {
        this.userStore = userStore;
        this.bookLibraryService = bookLibraryService;
        this.captchaService = captchaService;
        this.emailCodeService = emailCodeService;
        this.authSessionService = authSessionService;
        this.tokenTtlSeconds = tokenTtlHours * 3600;
    }

    public CaptchaService.CaptchaChallenge createCaptcha() {
        return captchaService.createChallenge();
    }

    public boolean checkCaptcha(String captchaId, String captchaAnswer) {
        return captchaService.matches(captchaId, captchaAnswer);
    }

    public String sendRegisterCode(String email, String captchaId, String captchaAnswer) {
        captchaService.validate(captchaId, captchaAnswer);
        if (userStore.findByEmail(email).isPresent()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "该邮箱已注册，请直接登录。");
        }
        return emailCodeService.sendRegisterCode(email.trim());
    }

    @Transactional
    public AuthResult register(String email, String password, String emailCode) {
        String normalizedEmail = normalizeEmail(email);
        if (userStore.findByEmail(normalizedEmail).isPresent()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "该邮箱已注册，请直接登录。");
        }
        emailCodeService.verifyRegisterCode(normalizedEmail, emailCode);
        validatePassword(password);

        String salt = randomSalt();
        UserAccount user = new UserAccount(
            UUID.randomUUID().toString(),
            normalizedEmail,
            hashPassword(password, salt),
            salt,
            Instant.now().toString()
        );
        userStore.save(user);
        bookLibraryService.ensureUserLibrary(user.id());
        String token = authSessionService.createSession(user.id(), tokenTtlSeconds);
        return new AuthResult(token, user.id(), user.email(), emailCodeService.isDevModeWithoutMail());
    }

    @Transactional
    public AuthResult login(String email, String password) {
        String normalizedEmail = normalizeEmail(email);
        UserAccount user = userStore.findByEmail(normalizedEmail)
            .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "邮箱或密码错误。"));
        validatePassword(password);
        if (!hashPassword(password, user.salt()).equals(user.passwordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "邮箱或密码错误。");
        }
        bookLibraryService.ensureUserLibrary(user.id());
        String token = authSessionService.createSession(user.id(), tokenTtlSeconds);
        return new AuthResult(token, user.id(), user.email(), emailCodeService.isDevModeWithoutMail());
    }

    public UserAccount requireUserByToken(String token) {
        var session = authSessionService.requireSession(token);
        return userStore.findById(session.getUserId())
            .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "登录已失效，请重新登录。"));
    }

    public void logout(String token) {
        authSessionService.revokeSession(token);
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 6) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "密码至少 6 位。");
        }
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "邮箱格式不正确。");
        }
        return email.trim().toLowerCase();
    }

    private String randomSalt() {
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    private String hashPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest((salt + password).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashed);
        } catch (Exception exception) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "密码处理失败。");
        }
    }

    public record AuthResult(String token, String userId, String email, boolean devMailMode) {
    }
}
