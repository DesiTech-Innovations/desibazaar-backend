-- Migration script for all entities in VyaparSathi (MySQL)
-- Drops and recreates all tables in dependency-safe order

DROP TABLE IF EXISTS reset_token;
DROP TABLE IF EXISTS refresh_token;
DROP TABLE IF EXISTS change_log;
DROP TABLE IF EXISTS audit_log;
DROP TABLE IF EXISTS notification;
DROP TABLE IF EXISTS payment;
DROP TABLE IF EXISTS sale_item;
DROP TABLE IF EXISTS sale;
DROP TABLE IF EXISTS expense;
DROP TABLE IF EXISTS purchase_order_item;
DROP TABLE IF EXISTS purchase_order;
DROP TABLE IF EXISTS stock_entry;
DROP TABLE IF EXISTS item_variant;
DROP TABLE IF EXISTS item;
DROP TABLE IF EXISTS supplier;
DROP TABLE IF EXISTS customer_ledger;
DROP TABLE IF EXISTS customer;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS shop;

CREATE TABLE shop (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    owner_name VARCHAR(255),
    address VARCHAR(255),
    state VARCHAR(255) NOT NULL,
    gstin VARCHAR(255),
    code VARCHAR(255) NOT NULL UNIQUE,
    locale VARCHAR(255),
    created_at DATETIME NOT NULL
);

CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL UNIQUE,
    pin_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50),
    active BOOLEAN NOT NULL
);

CREATE TABLE customer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
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
    notes VARCHAR(255),
    credit_balance DECIMAL(19,2) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE customer_ledger (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    type VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    created_at DATETIME NOT NULL,
    CONSTRAINT fk_customer_ledger_customer FOREIGN KEY (customer_id) REFERENCES customer(id)
);

CREATE TABLE supplier (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    contact_person VARCHAR(255),
    phone VARCHAR(255),
    email VARCHAR(255),
    address VARCHAR(255),
    gstin VARCHAR(255)
);

CREATE TABLE item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255),
    category VARCHAR(255),
    brand_name VARCHAR(255),
    created_at DATETIME
);

CREATE TABLE item_variant (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sku VARCHAR(255) NOT NULL UNIQUE,
    unit VARCHAR(255) NOT NULL,
    price_per_unit DECIMAL(19,2) NOT NULL,
    hsn VARCHAR(255),
    gst_rate INT,
    photo_path VARCHAR(255),
    item_id BIGINT NOT NULL,
    color VARCHAR(255),
    size VARCHAR(255),
    design VARCHAR(255),
    low_stock_threshold DECIMAL(19,2),
    CONSTRAINT fk_item_variant_item FOREIGN KEY (item_id) REFERENCES item(id)
);

CREATE TABLE stock_entry (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    item_variant_id BIGINT NOT NULL,
    quantity DECIMAL(19,2),
    cost_per_unit DECIMAL(19,2) NOT NULL,
    batch VARCHAR(255),
    last_updated DATETIME,
    CONSTRAINT fk_stock_entry_item_variant FOREIGN KEY (item_variant_id) REFERENCES item_variant(id)
);

CREATE TABLE purchase_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    po_number VARCHAR(255) NOT NULL UNIQUE,
    supplier_id BIGINT NOT NULL,
    order_date DATETIME NOT NULL,
    expected_delivery_date DATETIME,
    total_amount DECIMAL(19,2) NOT NULL,
    status VARCHAR(255) NOT NULL,
    CONSTRAINT fk_purchase_order_supplier FOREIGN KEY (supplier_id) REFERENCES supplier(id)
);

CREATE TABLE purchase_order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    purchase_order_id BIGINT NOT NULL,
    item_variant_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    unit_cost DECIMAL(19,2) NOT NULL,
    CONSTRAINT fk_purchase_order_item_order FOREIGN KEY (purchase_order_id) REFERENCES purchase_order(id),
    CONSTRAINT fk_purchase_order_item_variant FOREIGN KEY (item_variant_id) REFERENCES item_variant(id)
);

CREATE TABLE expense (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    shop_id BIGINT NOT NULL,
    type VARCHAR(255) NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    date DATETIME NOT NULL,
    notes VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE sale (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    invoice_no VARCHAR(255) NOT NULL UNIQUE,
    date DATETIME NOT NULL,
    shop_id BIGINT NOT NULL,
    customer_id BIGINT,
    total_amount DECIMAL(19,2) NOT NULL,
    cogs DECIMAL(19,2) NOT NULL DEFAULT 0,
    round_off DECIMAL(19,2),
    synced_flag BOOLEAN,
    CONSTRAINT fk_sale_shop FOREIGN KEY (shop_id) REFERENCES shop(id),
    CONSTRAINT fk_sale_customer FOREIGN KEY (customer_id) REFERENCES customer(id)
);

CREATE TABLE sale_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sale_id BIGINT NOT NULL,
    item_variant_id BIGINT NOT NULL,
    qty DECIMAL(19,2) NOT NULL,
    unit_price DECIMAL(19,2) NOT NULL,
    cost_per_unit DECIMAL(19,2) NOT NULL DEFAULT 0,
    taxable_value DECIMAL(19,2) NOT NULL,
    gst_type VARCHAR(50) NOT NULL,
    cgst_amt DECIMAL(19,2) NOT NULL,
    sgst_amt DECIMAL(19,2) NOT NULL,
    igst_amt DECIMAL(19,2) NOT NULL,
    CONSTRAINT fk_sale_item_sale FOREIGN KEY (sale_id) REFERENCES sale(id),
    CONSTRAINT fk_sale_item_variant FOREIGN KEY (item_variant_id) REFERENCES item_variant(id)
);

CREATE TABLE payment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sale_id BIGINT NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    amount_paid DECIMAL(19,2) NOT NULL,
    payment_status VARCHAR(50) NOT NULL,
    payment_date DATETIME NOT NULL,
    CONSTRAINT fk_payment_sale FOREIGN KEY (sale_id) REFERENCES sale(id)
);

CREATE TABLE notification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    type VARCHAR(255),
    message VARCHAR(255),
    recipient VARCHAR(255),
    `read` BOOLEAN,
    link VARCHAR(255),
    timestamp DATETIME
);

CREATE TABLE audit_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255),
    action VARCHAR(255),
    entity VARCHAR(255),
    entity_id VARCHAR(255),
    details VARCHAR(2000),
    timestamp DATETIME
);

CREATE TABLE change_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    entity_type VARCHAR(255) NOT NULL,
    entity_id BIGINT NOT NULL,
    operation VARCHAR(50) NOT NULL,
    payload_json TEXT,
    device_id VARCHAR(255) NOT NULL,
    seq_no BIGINT NOT NULL,
    created_at DATETIME NOT NULL
);

CREATE TABLE refresh_token (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    token VARCHAR(512) NOT NULL UNIQUE,
    username VARCHAR(255) NOT NULL,
    expiry_date DATETIME NOT NULL,
    created_at DATETIME NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE reset_token (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    token VARCHAR(512) NOT NULL UNIQUE,
    username VARCHAR(255) NOT NULL,
    expiry_date DATETIME NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL
);
