-- V5: 追读力系统表

-- 章节追读力评分
CREATE TABLE chapter_reading_power (
    id VARCHAR(36) PRIMARY KEY,
    book_id VARCHAR(36) NOT NULL,
    chapter_number INT NOT NULL,
    hook_type VARCHAR(50) COMMENT '钩子类型',
    hook_strength ENUM('strong', 'medium', 'weak') COMMENT '钩子强度',
    hook_content TEXT COMMENT '钩子内容',
    cool_points_json JSON COMMENT '爽点列表',
    micro_payoffs_json JSON COMMENT '微兑现列表',
    hard_violations_json JSON COMMENT '硬性违规',
    soft_violations_json JSON COMMENT '软性违规',
    overall_score DECIMAL(5,2) COMMENT '综合评分',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    UNIQUE INDEX idx_chapter_reading_power_book_chapter (book_id, chapter_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 债务追踪
CREATE TABLE chase_debt (
    id VARCHAR(36) PRIMARY KEY,
    book_id VARCHAR(36) NOT NULL,
    debt_type ENUM('promise', 'open_loop', 'foreshadowing') NOT NULL,
    subject VARCHAR(200) NOT NULL,
    description TEXT,
    created_chapter INT NOT NULL,
    urgency INT DEFAULT 50 COMMENT '紧急度 0-100',
    status ENUM('pending', 'paid_off', 'abandoned') DEFAULT 'pending',
    resolved_chapter INT,
    resolved_reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    INDEX idx_chase_debt_book_status (book_id, status),
    INDEX idx_chase_debt_urgency (urgency DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
