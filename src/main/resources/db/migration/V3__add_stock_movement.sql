-- Flyway migration: Add stock_movement table only (no drops)

CREATE TABLE IF NOT EXISTS stock_movement (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    item_variant_id BIGINT NOT NULL,
    movement_type VARCHAR(32) NOT NULL,
    quantity DECIMAL(19,4) NOT NULL,
    cost_per_unit DECIMAL(19,4),
    batch VARCHAR(255),
    reason VARCHAR(255),
    reference VARCHAR(255),
    timestamp DATETIME NOT NULL,
    CONSTRAINT fk_stock_movement_item_variant FOREIGN KEY (item_variant_id) REFERENCES item_variant(id) ON DELETE CASCADE
);
