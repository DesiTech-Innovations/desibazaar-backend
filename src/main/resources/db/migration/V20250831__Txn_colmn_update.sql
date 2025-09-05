-- Make transaction_id nullable
ALTER TABLE payment MODIFY transaction_id VARCHAR(255) NULL;

-- Create unique index on transaction_id (allows multiple NULLs)
CREATE UNIQUE INDEX uniq_transaction_id ON payment (transaction_id);