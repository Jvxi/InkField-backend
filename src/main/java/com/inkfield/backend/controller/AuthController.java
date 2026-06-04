package com.inkfield.backend.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inkfield.backend.service.AuthService;
import com.inkfield.backend.service.CaptchaService;
import com.inkfield.backend.service.EmailCodeService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final EmailCodeService emailCodeService;

    public AuthController(AuthService authService, EmailCodeService emailCodeService) {
        this.authService = authService;
        this.emailCodeService = emailCodeService;
    }

    @GetMapping("/captcha")
    public CaptchaService.CaptchaChallenge captcha() {
        return authService.createCaptcha();
    }

    @PostMapping("/captcha/check")
    public Map<String, Object> checkCaptcha(@RequestBody Map<String, String> payload) {
        boolean valid = authService.checkCaptcha(payload.get("captchaId"), payload.get("captchaAnswer"));
        return Map.of("valid", valid);
    }

    @PostMapping("/send-code")
    public Map<String, Object> sendCode(@RequestBody Map<String, String> payload) {
        String devCode = authService.sendRegisterCode(
            payload.get("email"),
            payload.get("captchaId"),
            payload.get("captchaAnswer")
        );
        boolean devMode = emailCodeService.isDevModeWithoutMail();
        java.util.HashMap<String, Object> body = new java.util.HashMap<>();
        body.put("ok", true);
        body.put(
            "message",
            devMode ? "验证码已生成（开发模式可直接复制下方验证码）" : "验证码已发送到邮箱（3 分钟内有效）"
        );
        if (devMode && devCode != null) {
            body.put("devCode", devCode);
        }
        return body;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Map<String, String> payload) {
        AuthService.AuthResult result = authService.register(
            payload.get("email"),
            payload.get("password"),
            payload.get("emailCode")
        );
        return authPayload(result);
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> payload) {
        AuthService.AuthResult result = authService.login(payload.get("email"), payload.get("password"));
        return authPayload(result);
    }

    @PostMapping("/logout")
    public Map<String, Boolean> logout(@RequestHeader(value = "Authorization", required = false) String authorization) {
        authService.logout(extractToken(authorization));
        return Map.of("ok", true);
    }

    @GetMapping("/me")
    public Map<String, Object> me(@RequestHeader(value = "Authorization", required = false) String authorization) {
        var user = authService.requireUserByToken(extractToken(authorization));
        return Map.of("userId", user.id(), "email", user.email());
    }

    private Map<String, Object> authPayload(AuthService.AuthResult result) {
        return Map.of(
            "token", result.token(),
            "userId", result.userId(),
            "email", result.email(),
            "devMailMode", result.devMailMode()
        );
    }

    private String extractToken(String authorization) {
        if (authorization == null) {
            return "";
        }
        if (authorization.startsWith("Bearer ")) {
            return authorization.substring(7).trim();
        }
        return authorization.trim();
    }
}
