CREATE TABLE news_items (
                            id BIGSERIAL PRIMARY KEY,
                            region VARCHAR(100) NOT NULL,
                            title VARCHAR(300) NOT NULL,
                            summary TEXT NOT NULL,
                            source_name VARCHAR(150),
                            source_url TEXT,
                            importance INTEGER NOT NULL DEFAULT 3,
                            news_date DATE NOT NULL,
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);