-- Additions and updates for Receiving, ReceivingItem, ReceivingTicket, ReceivingTicketAttachment, and StockEntry entities
-- Assumes you are running this on top of your previous schema

-- 1. Update receiving: add shop_id and supplier_id columns
ALTER TABLE receiving
    ADD COLUMN shop_id BIGINT NOT NULL AFTER id,
    ADD COLUMN supplier_id BIGINT NULL AFTER shop_id,
    ADD CONSTRAINT fk_receiving_shop_id FOREIGN KEY (shop_id) REFERENCES shop(id);

-- 2. Update receiving_item: add expected_qty, put_away_status, NOT NULL to received_qty
ALTER TABLE receiving_item
    ADD COLUMN expected_qty INTEGER NOT NULL DEFAULT 0 AFTER po_item_id,
    ADD COLUMN put_away_status VARCHAR(255) AFTER notes,
    MODIFY COLUMN received_qty INTEGER NOT NULL DEFAULT 0;

-- 3. Create receiving_ticket table
CREATE TABLE receiving_ticket (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    receiving_id BIGINT NOT NULL,
    reason VARCHAR(255),
    description TEXT,
    status VARCHAR(255),
    raised_at DATETIME,
    raised_by VARCHAR(255),
    FOREIGN KEY (receiving_id) REFERENCES receiving(id),
    INDEX idx_receiving_ticket_receiving_id (receiving_id)
);

-- 4. Create receiving_ticket_attachment table
CREATE TABLE receiving_ticket_attachment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    receiving_ticket_id BIGINT NOT NULL,
    file_name VARCHAR(255),
    file_type VARCHAR(255),
    file_path VARCHAR(1024),
    FOREIGN KEY (receiving_ticket_id) REFERENCES receiving_ticket(id),
    INDEX idx_receiving_ticket_attachment_ticket_id (receiving_ticket_id)
);

-- 5. Update stock_entry: add shop_id (if not present), batch, last_updated
ALTER TABLE stock_entry
    ADD COLUMN shop_id BIGINT NULL AFTER cost_per_unit,
    ADD CONSTRAINT fk_stock_entry_shop_id FOREIGN KEY (shop_id) REFERENCES shop(id);

-- (batch and last_updated already exist in your provided script, so not adding again)

-- 6. (If not already present) Add unique constraint on purchase_order.po_number
ALTER TABLE purchase_order
    ADD CONSTRAINT uc_purchase_order_po_number UNIQUE (po_number);

-- 7. (Optional) Add missing indexes for performance
CREATE INDEX idx_receiving_shop_id ON receiving(shop_id);
CREATE INDEX idx_stock_entry_shop_id ON stock_entry(shop_id);

-- END OF SCRIPT