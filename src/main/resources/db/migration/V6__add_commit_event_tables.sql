-- V6: 章节提交与事件审计表

-- 章节提交记录
CREATE TABLE chapter_commit (
    id VARCHAR(36) PRIMARY KEY,
    book_id VARCHAR(36) NOT NULL,
    chapter_number INT NOT NULL,
    status ENUM('accepted', 'rejected') NOT NULL,
    contract_refs_json JSON COMMENT '合同引用：master, volume, chapter, review',
    outline_snapshot_json JSON COMMENT '大纲快照：planned_nodes, covered_nodes, missed_nodes, extra_nodes',
    review_result_json JSON COMMENT '审查结果',
    fulfillment_result_json JSON COMMENT '履约结果',
    disambiguation_result_json JSON COMMENT '消歧结果',
    summary_text TEXT COMMENT '章节摘要',
    dominant_strand VARCHAR(50) COMMENT '主导线索',
    projection_status_json JSON COMMENT '投影状态：state, index, summary, memory, vector',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    INDEX idx_chapter_commit_book_chapter (book_id, chapter_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 故事事件审计
CREATE TABLE story_event (
    id VARCHAR(36) PRIMARY KEY,
    book_id VARCHAR(36) NOT NULL,
    chapter_number INT NOT NULL,
    event_type ENUM(
        'character_state_changed',
        'relationship_changed',
        'world_rule_revealed',
        'world_rule_broken',
        'power_breakthrough',
        'artifact_obtained',
        'promise_created',
        'promise_paid_off',
        'open_loop_created',
        'open_loop_closed'
    ) NOT NULL,
    subject VARCHAR(200) COMMENT '事件主体（实体ID或主题）',
    field VARCHAR(200) COMMENT '属性名',
    old_value TEXT COMMENT '旧值',
    new_value TEXT COMMENT '新值',
    reason TEXT COMMENT '原因',
    payload_json JSON COMMENT '扩展元数据',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    INDEX idx_story_event_book_chapter (book_id, chapter_number),
    INDEX idx_story_event_type (event_type),
    INDEX idx_story_event_subject (subject)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
