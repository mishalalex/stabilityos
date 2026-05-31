CREATE TABLE health_logs (
    id BIGSERIAL PRIMARY KEY ,
    sleep_hours NUMERIC(4, 2),
    water_liters NUMERIC(4, 2),
    weight_kg NUMERIC(5, 2),
    mood VARCHAR(50),
    notes TEXT,
    entry_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);