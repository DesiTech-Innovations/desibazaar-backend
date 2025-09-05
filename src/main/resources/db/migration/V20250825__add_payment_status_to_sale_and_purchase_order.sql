-- Migration script: Add payment_status column to sale and purchase_order tables

ALTER TABLE `sale`
    ADD COLUMN `payment_status` VARCHAR(32) DEFAULT 'PENDING' AFTER `total_amount`;

ALTER TABLE `purchase_order`
    ADD COLUMN `payment_status` VARCHAR(32) DEFAULT 'PENDING' AFTER `total_amount`;

-- Optional: If you want to update existing records to PENDING explicitly
UPDATE `sale` SET `payment_status` = 'PENDING' WHERE `payment_status` IS NULL;
UPDATE `purchase_order` SET `payment_status` = 'PENDING' WHERE `payment_status` IS NULL;