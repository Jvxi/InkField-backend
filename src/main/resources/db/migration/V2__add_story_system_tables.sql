-- V2: Story System 合同系统表

-- 主设定合同（每书一份）
CREATE TABLE story_master_setting (
    id VARCHAR(36) PRIMARY KEY,
    book_id VARCHAR(36) NOT NULL,
    route_json JSON COMMENT '题材路由：primary_genre, canonical_genre, genre_filter',
    master_constraints_json JSON COMMENT '核心约束：core_tone, pacing_strategy',
    base_context_json JSON COMMENT '基础上下文（CSV检索结果）',
    override_policy_json JSON COMMENT '覆盖策略：locked, append_only, override_allowed',
    source_trace_json JSON COMMENT '来源追溯',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    INDEX idx_story_master_setting_book_id (book_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 卷级合同
CREATE TABLE story_volume_brief (
    id VARCHAR(36) PRIMARY KEY,
    book_id VARCHAR(36) NOT NULL,
    volume_number INT NOT NULL,
    volume_goal_json JSON COMMENT '卷目标',
    selected_tropes_json JSON COMMENT '选中的桥段',
    selected_pacing_json JSON COMMENT '节奏配置',
    selected_scenes_json JSON COMMENT '场景列表',
    anti_patterns_json JSON COMMENT '反模式',
    system_constraints_json JSON COMMENT '系统约束',
    overrides_json JSON COMMENT '覆盖规则',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    UNIQUE INDEX idx_story_volume_brief_book_volume (book_id, volume_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 章节级合同
CREATE TABLE story_chapter_brief (
    id VARCHAR(36) PRIMARY KEY,
    book_id VARCHAR(36) NOT NULL,
    chapter_number INT NOT NULL,
    chapter_directive_json JSON COMMENT '章节指令：goal, time_anchor, chapter_span, countdown',
    dynamic_context_json JSON COMMENT '动态上下文（CSV检索结果）',
    reasoning_json JSON COMMENT '推理层：genre, inject_target, style_priority, pacing_strategy',
    override_allowed_json JSON COMMENT '允许覆盖的字段',
    source_trace_json JSON COMMENT '来源追溯',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    UNIQUE INDEX idx_story_chapter_brief_book_chapter (book_id, chapter_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 审查合同
CREATE TABLE story_review_contract (
    id VARCHAR(36) PRIMARY KEY,
    book_id VARCHAR(36) NOT NULL,
    chapter_number INT NOT NULL,
    must_check_json JSON COMMENT '必检项',
    blocking_rules_json JSON COMMENT '阻断规则',
    genre_specific_risks_json JSON COMMENT '题材特定风险',
    anti_patterns_json JSON COMMENT '反模式',
    review_thresholds_json JSON COMMENT '审查阈值',
    overrides_json JSON COMMENT '覆盖规则',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    UNIQUE INDEX idx_story_review_contract_book_chapter (book_id, chapter_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
