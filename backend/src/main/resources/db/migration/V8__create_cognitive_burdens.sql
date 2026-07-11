CREATE TABLE cognitive_burdens (
                                   id BIGSERIAL PRIMARY KEY,
                                   input_item_id BIGINT REFERENCES input_items(id),
                                   title VARCHAR(200) NOT NULL,
                                   description TEXT,
                                   burden_type VARCHAR(50) NOT NULL,
                                   status VARCHAR(50) NOT NULL,
                                   burden_score INTEGER NOT NULL,
                                   next_action TEXT,
                                   created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   resolved_at TIMESTAMP,
                                   resolution_note TEXT
);