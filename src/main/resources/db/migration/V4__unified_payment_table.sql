-- V5__unified_payment_table.sql

CREATE TABLE payment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    source_id BIGINT NOT NULL,
    source_type VARCHAR(50) NOT NULL,
    supplier_id BIGINT,
    customer_id BIGINT,
    amount DECIMAL(15,2) NOT NULL,
    payment_date DATETIME NOT NULL,
    payment_method VARCHAR(50),
    reference VARCHAR(100),
    notes VARCHAR(255),
    status VARCHAR(20) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by VARCHAR(100),
    INDEX idx_source (source_type, source_id),
    INDEX idx_supplier (supplier_id),
    INDEX idx_customer (customer_id)
);

-- Data migration example (adjust as needed):
-- INSERT INTO payment (source_id, source_type, supplier_id, amount, payment_date, payment_method, reference, notes, status)
-- SELECT po_id, 'PURCHASE_ORDER', supplier_id, amount, payment_date, method, reference, notes, status FROM supplier_payment;
-- INSERT INTO payment (source_id, source_type, customer_id, amount, payment_date, payment_method, reference, notes, status)
-- SELECT sale_id, 'SALE', customer_id, amount, payment_date, method, reference, notes, status FROM sale_payment;

-- DROP TABLE supplier_payment;
-- DROP TABLE sale_payment;
