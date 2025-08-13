CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL UNIQUE,
    pin_hash VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE reset_tokens (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    token VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(255) NOT NULL,
    expiry DATETIME NOT NULL
);

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

CREATE TABLE item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at DATETIME
);

CREATE TABLE item_variant (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sku VARCHAR(255) NOT NULL UNIQUE,
    unit VARCHAR(255) NOT NULL,
    price_per_unit DECIMAL(19, 2) NOT NULL,
    hsn VARCHAR(255),
    gst_rate INT NOT NULL,
    photo_path VARCHAR(255),
    item_id BIGINT NOT NULL,
    color VARCHAR(255),
    size VARCHAR(255),
    design VARCHAR(255),
    FOREIGN KEY (item_id) REFERENCES item(id)
);

CREATE TABLE change_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    entity_type VARCHAR(255) NOT NULL,
    entity_id BIGINT NOT NULL,
    operation VARCHAR(255) NOT NULL,
    payload_json TEXT,
    device_id VARCHAR(255) NOT NULL,
    seq_no INT NOT NULL,
    created_at DATETIME NOT NULL
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
    credit_balance DECIMAL(19, 2) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE customer_ledger (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    customer_id BIGINT NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    type VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    created_at DATETIME NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customer(id)
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

CREATE TABLE purchase_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    po_number VARCHAR(255) NOT NULL UNIQUE,
    supplier_id BIGINT NOT NULL,
    order_date DATETIME NOT NULL,
    expected_delivery_date DATETIME,
    total_amount DECIMAL(19, 2) NOT NULL,
    status VARCHAR(255) NOT NULL,
    FOREIGN KEY (supplier_id) REFERENCES supplier(id)
);

CREATE TABLE purchase_order_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    purchase_order_id BIGINT NOT NULL,
    item_variant_id BIGINT NOT NULL,
    quantity DECIMAL(19, 2) NOT NULL,
    unit_cost DECIMAL(19, 2) NOT NULL,
    FOREIGN KEY (purchase_order_id) REFERENCES purchase_order(id),
    FOREIGN KEY (item_variant_id) REFERENCES item_variant(id)
);

CREATE TABLE stock_entry (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    item_variant_id BIGINT NOT NULL,
    quantity DECIMAL(19, 2),
    batch VARCHAR(255),
    last_updated DATETIME,
    FOREIGN KEY (item_variant_id) REFERENCES item_variant(id)
);

CREATE TABLE expense (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    shop_id BIGINT NOT NULL,
    type VARCHAR(255) NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    date DATETIME NOT NULL,
    notes VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (shop_id) REFERENCES shop(id)
);

CREATE TABLE sale (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    invoice_no VARCHAR(255) NOT NULL UNIQUE,
    date DATETIME NOT NULL,
    shop_id BIGINT NOT NULL,
    customer_id BIGINT,
    total_amount DECIMAL(19, 2) NOT NULL,
    round_off DECIMAL(19, 2),
    payment_method VARCHAR(255),
    synced_flag BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (shop_id) REFERENCES shop(id),
    FOREIGN KEY (customer_id) REFERENCES customer(id)
);

CREATE TABLE sale_item (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sale_id BIGINT NOT NULL,
    item_variant_id BIGINT NOT NULL,
    qty DECIMAL(19, 2) NOT NULL,
    unit_price DECIMAL(19, 2) NOT NULL,
    taxable_value DECIMAL(19, 2) NOT NULL,
    gst_rate INT NOT NULL,
    cgst_amt DECIMAL(19, 2) NOT NULL,
    sgst_amt DECIMAL(19, 2) NOT NULL,
    igst_amt DECIMAL(19, 2) NOT NULL,
    FOREIGN KEY (sale_id) REFERENCES sale(id),
    FOREIGN KEY (item_variant_id) REFERENCES item_variant(id)
);
