CREATE TABLE commitments (
                             id BIGSERIAL PRIMARY KEY,
                             open_loop_id BIGINT REFERENCES open_loops(id),
                             title VARCHAR(200) NOT NULL,
                             description TEXT,
                             commitment_type VARCHAR(50) NOT NULL,
                             status VARCHAR(50) NOT NULL,
                             priority VARCHAR(50) NOT NULL,
                             due_date DATE,
                             completed_at TIMESTAMP,
                             dropped_at TIMESTAMP,
                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             outcome_note TEXT
);