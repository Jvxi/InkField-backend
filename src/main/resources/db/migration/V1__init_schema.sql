CREATE TABLE users (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(64) NOT NULL,
    salt VARCHAR(32) NOT NULL,
    created_at VARCHAR(30) NOT NULL,
    CONSTRAINT uk_users_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE auth_sessions (
    token VARCHAR(128) NOT NULL PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    expires_at BIGINT NOT NULL,
    INDEX idx_auth_sessions_user (user_id),
    INDEX idx_auth_sessions_expires (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE email_verification_codes (
    email VARCHAR(255) NOT NULL PRIMARY KEY,
    code VARCHAR(10) NOT NULL,
    expires_at BIGINT NOT NULL,
    last_sent_at BIGINT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE captcha_challenges (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    answer VARCHAR(16) NOT NULL,
    expires_at BIGINT NOT NULL,
    INDEX idx_captcha_expires (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE user_libraries (
    user_id VARCHAR(36) NOT NULL PRIMARY KEY,
    active_book_id VARCHAR(36) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE books (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    title VARCHAR(255) NOT NULL DEFAULT '',
    genre VARCHAR(128) NOT NULL DEFAULT '',
    updated_at VARCHAR(30) NOT NULL,
    chapter_count INT NOT NULL DEFAULT 0,
    onboarding_completed TINYINT(1) NOT NULL DEFAULT 0,
    project_json LONGTEXT NOT NULL,
    INDEX idx_books_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
