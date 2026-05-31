CREATE TABLE input_items (
                             id BIGSERIAL PRIMARY KEY,
                             source VARCHAR(50) NOT NULL,
                             input_type VARCHAR(50) NOT NULL,
                             raw_text TEXT,
                             file_path TEXT,
                             telegram_message_id VARCHAR(100),
                             status VARCHAR(50) NOT NULL,
                             detected_domain VARCHAR(50),
                             error_message TEXT,
                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             processed_at TIMESTAMP
);