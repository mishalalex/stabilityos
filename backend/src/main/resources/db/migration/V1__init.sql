CREATE TABLE app_metadata (
                              id BIGSERIAL PRIMARY KEY,
                              metadata_key VARCHAR(100) NOT NULL UNIQUE,
                              metadata_value TEXT NOT NULL,
                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);