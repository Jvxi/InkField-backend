package com.novelstudio.backend.auth;

import org.springframework.http.HttpStatus;

import com.novelstudio.backend.exception.ApiException;

public final class UserContext {
    private static final ThreadLocal<String> USER_ID = new ThreadLocal<>();

    private UserContext() {
    }

    public static void setUserId(String userId) {
        USER_ID.set(userId);
    }

    public static String requireUserId() {
        String userId = USER_ID.get();
        if (userId == null || userId.isBlank()) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "请先登录后再操作。");
        }
        return userId;
    }

    public static void clear() {
        USER_ID.remove();
    }
}
