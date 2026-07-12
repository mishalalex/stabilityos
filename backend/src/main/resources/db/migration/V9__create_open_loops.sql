CREATE TABLE open_loops (
                            id BIGSERIAL PRIMARY KEY,
                            input_item_id BIGINT REFERENCES input_items(id),
                            cognitive_burden_id BIGINT REFERENCES cognitive_burdens(id),
                            title VARCHAR(200) NOT NULL,
                            description TEXT,
                            loop_type VARCHAR(50) NOT NULL,
                            status VARCHAR(50) NOT NULL,
                            closure_condition TEXT NOT NULL,
                            next_action TEXT,
                            next_review_date DATE,
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            closed_at TIMESTAMP,
                            closure_note TEXT
);