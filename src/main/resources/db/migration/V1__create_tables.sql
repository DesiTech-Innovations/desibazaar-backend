CREATE TABLE IF NOT EXISTS shop (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    address TEXT,
    state TEXT NOT NULL,
    gstin TEXT,
    code TEXT NOT NULL,
    locale TEXT,
    created_at DATETIME
);

CREATE TABLE IF NOT EXISTS item (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    sku TEXT UNIQUE NOT NULL,
    name TEXT NOT NULL,
    unit TEXT NOT NULL,  -- e.g., 'meter', 'piece'
    price_per_unit DECIMAL(10,2) NOT NULL,
    hsn TEXT,
    gst_rate INTEGER NOT NULL,  -- e.g., 5, 12
    photo_path TEXT
);

CREATE TABLE IF NOT EXISTS stock_entry (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    item_id INTEGER NOT NULL,
    quantity DECIMAL(10,2) NOT NULL,
    batch TEXT,
    last_updated DATETIME,
    FOREIGN KEY (item_id) REFERENCES item(id)
);

CREATE TABLE IF NOT EXISTS customer (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    phone TEXT,
    address TEXT,
    state TEXT,
    gstin TEXT,
    outstanding DECIMAL(10,2) DEFAULT 0.0
);

CREATE TABLE IF NOT EXISTS sale (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    invoice_no TEXT UNIQUE NOT NULL,
    date DATETIME NOT NULL,
    shop_id INTEGER NOT NULL,
    customer_id INTEGER,
    total_amount DECIMAL(10,2) NOT NULL,
    round_off DECIMAL(10,2) DEFAULT 0.0,
    payment_method TEXT,
    synced_flag BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (shop_id) REFERENCES shop(id),
    FOREIGN KEY (customer_id) REFERENCES customer(id)
);

CREATE TABLE IF NOT EXISTS sale_item (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    sale_id INTEGER NOT NULL,
    item_id INTEGER NOT NULL,
    qty DECIMAL(10,2) NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    taxable_value DECIMAL(10,2) NOT NULL,
    gst_rate INTEGER NOT NULL,
    cgst_amt DECIMAL(10,2),
    sgst_amt DECIMAL(10,2),
    igst_amt DECIMAL(10,2),
    FOREIGN KEY (sale_id) REFERENCES sale(id),
    FOREIGN KEY (item_id) REFERENCES item(id)
);