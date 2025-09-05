-- V2__Add_Categories_And_Attributes.sql

-- Step 1: Create the new 'categories' table for hierarchical data.
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    parent_id BIGINT,
    CONSTRAINT fk_category_parent FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE SET NULL
);

-- Step 2: Add the new clothing attribute columns to existing tables.
ALTER TABLE item ADD COLUMN fabric VARCHAR(255);
ALTER TABLE item ADD COLUMN season VARCHAR(255);
ALTER TABLE item_variant ADD COLUMN fit VARCHAR(255);

-- Step 3: Add the new 'category_id' foreign key column to the 'item' table.
ALTER TABLE item ADD COLUMN category_id BIGINT;
ALTER TABLE item ADD CONSTRAINT fk_item_category FOREIGN KEY (category_id) REFERENCES categories(id);

-- Step 4: (Optional but Recommended) After migrating your data, you can drop the old column.
-- You should run this command MANUALLY after you are sure your data migration script has worked.
-- ALTER TABLE item DROP COLUMN category;
