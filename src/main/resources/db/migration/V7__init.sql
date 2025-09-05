-- Drop tables in reverse dependency order to avoid FK errors.
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS sale_item;
DROP TABLE IF EXISTS sale;
DROP TABLE IF EXISTS shop;
DROP TABLE IF EXISTS payment;
DROP TABLE IF EXISTS receiving_item;
DROP TABLE IF EXISTS receiving;
DROP TABLE IF EXISTS purchase_order_item;
DROP TABLE IF EXISTS purchase_order;
DROP TABLE IF EXISTS supplier;
DROP TABLE IF EXISTS stock_movement;
DROP TABLE IF EXISTS stock_entry;
DROP TABLE IF EXISTS item_variant;
DROP TABLE IF EXISTS item;
DROP TABLE IF EXISTS expense;
DROP TABLE IF EXISTS customer_ledger;
DROP TABLE IF EXISTS customer;
DROP TABLE IF EXISTS change_log;
DROP TABLE IF EXISTS refresh_token;
DROP TABLE IF EXISTS reset_token;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS audit_log;
DROP TABLE IF EXISTS notification;
SET FOREIGN_KEY_CHECKS = 1;

-- Audit Log
CREATE TABLE audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255),
    action VARCHAR(255),
    entity VARCHAR(255),
    entity_id VARCHAR(255),
    details VARCHAR(2000),
    timestamp DATETIME,
    INDEX idx_audit_entity (entity, entity_id)
);

-- Users
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    pin_hash VARCHAR(255) NOT NULL,
    role VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Reset Token
CREATE TABLE reset_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) UNIQUE NOT NULL,
    username VARCHAR(255) NOT NULL,
    expiry DATETIME NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE
);

-- Refresh Token
CREATE TABLE refresh_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) UNIQUE NOT NULL,
    username VARCHAR(255) NOT NULL,
    expiry_date DATETIME NOT NULL,
    created_at DATETIME NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE
);

-- Change Log
CREATE TABLE change_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_type VARCHAR(255) NOT NULL,
    entity_id BIGINT NOT NULL,
    operation VARCHAR(255) NOT NULL,
    payload_json TEXT,
    device_id VARCHAR(255) NOT NULL,
    seq_no BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    INDEX idx_change_entity (entity_type, entity_id)
);

-- Customer
CREATE TABLE customer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(15) UNIQUE,
    email VARCHAR(255),
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    city VARCHAR(255),
    state VARCHAR(255),
    postal_code VARCHAR(255),
    country VARCHAR(255),
    gst_number VARCHAR(255),
    pan_number VARCHAR(255),
    notes TEXT,
    credit_balance DECIMAL(10,2) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE INDEX idx_customer_phone ON customer(phone);

-- Customer Ledger
CREATE TABLE customer_ledger (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    type VARCHAR(255) NOT NULL,
    description TEXT,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customer(id),
    INDEX idx_customer_ledger_customer_id (customer_id)
);

-- Expense
CREATE TABLE expense (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    shop_id BIGINT NOT NULL,
    type VARCHAR(255) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    date DATETIME NOT NULL,
    notes TEXT,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_expense_shop_id ON expense(shop_id);

-- Item
CREATE TABLE item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL,
    description TEXT,
    category VARCHAR(255),
    brand_name VARCHAR(255),
    created_at DATETIME
);

-- Item Variant
CREATE TABLE item_variant (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sku VARCHAR(255) UNIQUE NOT NULL,
    unit VARCHAR(255) NOT NULL,
    price_per_unit DECIMAL(10,2) NOT NULL,
    hsn VARCHAR(255),
    gst_rate INTEGER,
    photo_path VARCHAR(255),
    item_id BIGINT NOT NULL,
    color VARCHAR(255),
    size VARCHAR(255),
    design VARCHAR(255),
    low_stock_threshold DECIMAL(10,2),
    FOREIGN KEY (item_id) REFERENCES item(id),
    INDEX idx_item_variant_item_id (item_id)
);

-- Stock Entry
CREATE TABLE stock_entry (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_variant_id BIGINT NOT NULL,
    quantity DECIMAL(10,2),
    cost_per_unit DECIMAL(10,2) NOT NULL,
    batch VARCHAR(255),
    last_updated DATETIME,
    FOREIGN KEY (item_variant_id) REFERENCES item_variant(id),
    INDEX idx_stock_entry_item_variant_id (item_variant_id)
);

-- Stock Movement
CREATE TABLE stock_movement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_variant_id BIGINT NOT NULL,
    movement_type VARCHAR(255) NOT NULL,
    quantity DECIMAL(10,2) NOT NULL,
    cost_per_unit DECIMAL(10,2),
    batch VARCHAR(255),
    reason VARCHAR(255),
    reference VARCHAR(255),
    timestamp DATETIME NOT NULL,
    FOREIGN KEY (item_variant_id) REFERENCES item_variant(id),
    INDEX idx_stock_movement_item_variant_id (item_variant_id)
);

-- Supplier
CREATE TABLE supplier (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    contact_person VARCHAR(255),
    phone VARCHAR(255),
    email VARCHAR(255),
    address VARCHAR(255),
    gstin VARCHAR(255)
);

CREATE INDEX idx_supplier_phone ON supplier(phone);

-- Purchase Order
CREATE TABLE purchase_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    po_number VARCHAR(255) UNIQUE NOT NULL,
    supplier_id BIGINT NOT NULL,
    order_date DATETIME NOT NULL,
    expected_delivery_date DATETIME,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(255) NOT NULL,
    notes TEXT,
    FOREIGN KEY (supplier_id) REFERENCES supplier(id),
    INDEX idx_purchase_order_supplier_id (supplier_id)
);

-- Purchase Order Item
CREATE TABLE purchase_order_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    purchase_order_id BIGINT NOT NULL,
    item_variant_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    unit_cost DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (purchase_order_id) REFERENCES purchase_order(id),
    FOREIGN KEY (item_variant_id) REFERENCES item_variant(id),
    INDEX idx_purchase_order_item_order_id (purchase_order_id),
    INDEX idx_purchase_order_item_variant_id (item_variant_id)
);

-- Receiving
CREATE TABLE receiving (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    purchase_order_id BIGINT NOT NULL,
    status VARCHAR(255),
    received_at DATETIME,
    received_by VARCHAR(255),
    notes TEXT,
    FOREIGN KEY (purchase_order_id) REFERENCES purchase_order(id),
    INDEX idx_receiving_purchase_order_id (purchase_order_id)
);

-- Receiving Item
CREATE TABLE receiving_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    receiving_id BIGINT NOT NULL,
    po_item_id BIGINT NOT NULL,
    status VARCHAR(255),
    received_qty INTEGER,
    damaged_qty INTEGER,
    damage_reason VARCHAR(255),
    notes TEXT,
    FOREIGN KEY (receiving_id) REFERENCES receiving(id),
    FOREIGN KEY (po_item_id) REFERENCES purchase_order_item(id),
    INDEX idx_receiving_item_receiving_id (receiving_id),
    INDEX idx_receiving_item_po_item_id (po_item_id)
);

-- Payment
CREATE TABLE payment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_id VARCHAR(255) UNIQUE,
    source_id BIGINT,
    source_type VARCHAR(255),
    supplier_id BIGINT,
    customer_id BIGINT,
    amount DECIMAL(10,2),
    payment_date DATETIME,
    payment_method VARCHAR(255),
    reference VARCHAR(255),
    notes TEXT,
    status VARCHAR(255),
    INDEX idx_payment_source (source_type, source_id),
    INDEX idx_payment_supplier (supplier_id),
    INDEX idx_payment_customer (customer_id)
);

-- Shop
CREATE TABLE shop (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    owner_name VARCHAR(255),
    address VARCHAR(255),
    state VARCHAR(255) NOT NULL,
    gstin VARCHAR(255),
    code VARCHAR(255) UNIQUE NOT NULL,
    locale VARCHAR(255),
    created_at DATETIME NOT NULL
);

-- Sale
CREATE TABLE sale (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_no VARCHAR(255) UNIQUE NOT NULL,
    date DATETIME NOT NULL,
    shop_id BIGINT NOT NULL,
    customer_id BIGINT,
    total_amount DECIMAL(10,2) NOT NULL,
    cogs DECIMAL(10,2) NOT NULL,
    round_off DECIMAL(10,2),
    synced_flag BOOLEAN,
    FOREIGN KEY (shop_id) REFERENCES shop(id),
    FOREIGN KEY (customer_id) REFERENCES customer(id),
    INDEX idx_sale_shop_customer (shop_id, customer_id)
);

-- Sale Item
CREATE TABLE sale_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sale_id BIGINT NOT NULL,
    item_variant_id BIGINT NOT NULL,
    qty DECIMAL(10,2) NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    cost_per_unit DECIMAL(10,2) NOT NULL,
    taxable_value DECIMAL(10,2) NOT NULL,
    gst_type VARCHAR(255) NOT NULL,
    cgst_amt DECIMAL(10,2) NOT NULL,
    sgst_amt DECIMAL(10,2) NOT NULL,
    igst_amt DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (sale_id) REFERENCES sale(id),
    FOREIGN KEY (item_variant_id) REFERENCES item_variant(id),
    INDEX idx_sale_item_sale_id (sale_id),
    INDEX idx_sale_item_variant_id (item_variant_id)
);

-- Notification
CREATE TABLE notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(255),
    message VARCHAR(255),
    recipient VARCHAR(255),
    `read` BOOLEAN,
    link VARCHAR(255),
    timestamp DATETIME
);