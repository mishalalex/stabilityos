CREATE TABLE action_drafts (
                               id BIGSERIAL PRIMARY KEY,
                               input_item_id BIGINT REFERENCES input_items(id),
                               draft_type VARCHAR(50) NOT NULL,
                               title VARCHAR(200) NOT NULL,
                               proposed_action TEXT NOT NULL,
                               status VARCHAR(50) NOT NULL,
                               created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               decided_at TIMESTAMP,
                               decision_note TEXT
);