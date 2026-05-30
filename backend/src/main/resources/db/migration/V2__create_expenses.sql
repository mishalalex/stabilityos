CREATE TABLE expenses (
                          id BIGSERIAL PRIMARY KEY,
                          amount NUMERIC(12, 2) NOT NULL,
                          category VARCHAR(100) NOT NULL,
                          note TEXT,
                          entry_date DATE NOT NULL,
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);