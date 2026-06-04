-- V4: RAG智能上下文系统表

-- 向量嵌入存储
CREATE TABLE vector_embedding (
    id VARCHAR(36) PRIMARY KEY,
    book_id VARCHAR(36) NOT NULL,
    chunk_id VARCHAR(100) NOT NULL COMMENT '块ID（如 ch0001_p3）',
    chunk_text TEXT NOT NULL COMMENT '原始文本',
    embedding BLOB NOT NULL COMMENT '向量嵌入（float32字节）',
    entity_names_json JSON COMMENT '涉及的实体名称列表',
    chapter_number INT COMMENT '来源章节',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    UNIQUE INDEX idx_vector_embedding_book_chunk (book_id, chunk_id),
    INDEX idx_vector_embedding_book_chapter (book_id, chapter_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- BM25索引（分词后的词项）
CREATE TABLE bm25_term (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_id VARCHAR(36) NOT NULL,
    chunk_id VARCHAR(100) NOT NULL,
    term VARCHAR(50) NOT NULL,
    term_frequency INT NOT NULL,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    INDEX idx_bm25_term_book_term (book_id, term),
    INDEX idx_bm25_term_chunk (chunk_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- RAG查询日志
CREATE TABLE rag_query_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_id VARCHAR(36) NOT NULL,
    chapter_number INT,
    query_text TEXT NOT NULL,
    query_type ENUM('vector', 'bm25', 'hybrid', 'graph_hybrid') NOT NULL,
    results_json JSON COMMENT '检索结果',
    latency_ms INT COMMENT '查询耗时',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    INDEX idx_rag_query_log_book_chapter (book_id, chapter_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
