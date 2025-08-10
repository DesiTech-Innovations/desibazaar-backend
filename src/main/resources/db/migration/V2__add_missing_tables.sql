CREATE TABLE IF NOT EXISTS change_log (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    entity_type TEXT,
    entity_id INTEGER,
    operation TEXT,
    payload_json TEXT,
    device_id TEXT,
    seq_no INTEGER,
    created_at DATETIME
);

CREATE TABLE IF NOT EXISTS expense (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    shop_id INTEGER,
    type TEXT,
    amount DECIMAL(10,2),
    date DATETIME,
    notes TEXT,
    FOREIGN KEY (shop_id) REFERENCES shop(id)
);

-- Add soft-delete to sale if needed
ALTER TABLE sale ADD COLUMN deleted BOOLEAN DEFAULT FALSE;