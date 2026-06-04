-- V3: 长期记忆系统表

-- 记忆项（7个桶统一存储）
CREATE TABLE memory_item (
    id VARCHAR(36) PRIMARY KEY,
    book_id VARCHAR(36) NOT NULL,
    layer ENUM('semantic', 'episodic') NOT NULL,
    category ENUM(
        'character_state',
        'story_fact',
        'world_rule',
        'timeline',
        'open_loop',
        'reader_promise',
        'relationship'
    ) NOT NULL,
    subject VARCHAR(200) NOT NULL COMMENT '实体ID或主题',
    field VARCHAR(200) COMMENT '属性名',
    value TEXT COMMENT '当前值',
    payload_json JSON COMMENT '扩展元数据（含old_value等）',
    status ENUM('active', 'outdated', 'contradicted', 'tentative') DEFAULT 'active',
    source_chapter INT COMMENT '来源章节',
    evidence_json JSON COMMENT '证据引用',
    dedup_key VARCHAR(255) COMMENT '去重键（基于category规则生成）',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    INDEX idx_memory_item_book_category (book_id, category),
    INDEX idx_memory_item_book_subject (book_id, subject),
    INDEX idx_memory_item_dedup_key (book_id, dedup_key),
    INDEX idx_memory_item_status (status),
    INDEX idx_memory_item_source_chapter (source_chapter)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 章节摘要
CREATE TABLE chapter_summary (
    id VARCHAR(36) PRIMARY KEY,
    book_id VARCHAR(36) NOT NULL,
    chapter_number INT NOT NULL,
    summary_text TEXT NOT NULL,
    hook_json JSON COMMENT '钩子信息：type, content, strength',
    pattern_json JSON COMMENT '模式信息',
    ending_json JSON COMMENT '结尾信息',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    UNIQUE INDEX idx_chapter_summary_book_chapter (book_id, chapter_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
