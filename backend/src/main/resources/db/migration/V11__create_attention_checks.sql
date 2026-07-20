CREATE TABLE attention_checks (
                                  id BIGSERIAL PRIMARY KEY,
                                  commitment_id BIGINT REFERENCES commitments(id),
                                  source VARCHAR(50) NOT NULL,
                                  activity_type VARCHAR(50) NOT NULL,
                                  title VARCHAR(200) NOT NULL,
                                  description TEXT,
                                  urgency_score INTEGER NOT NULL,
                                  importance_score INTEGER NOT NULL,
                                  decision VARCHAR(50) NOT NULL,
                                  decision_reason TEXT NOT NULL,
                                  recommended_action TEXT NOT NULL,
                                  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);