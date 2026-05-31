CREATE TABLE assistant_memory (
                                  id BIGSERIAL PRIMARY KEY,
                                  memory_key VARCHAR(150) NOT NULL UNIQUE,
                                  memory_value TEXT NOT NULL,
                                  memory_type VARCHAR(50) NOT NULL,
                                  importance INTEGER NOT NULL DEFAULT 1,
                                  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);