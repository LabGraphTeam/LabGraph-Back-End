-- Wrap all statements in a single transaction
START TRANSACTION;

-- Convert user_roles from BOOLEAN to VARCHAR
ALTER TABLE users
    MODIFY COLUMN user_roles VARCHAR(25) NOT NULL DEFAULT 'USER';

-- Update any existing user records where user_roles was true (representing ADMIN)
-- and set them to the string value 'ADMIN'
UPDATE users
SET user_roles = 'ADMIN'
WHERE user_roles = 'true' OR user_roles = '1';

-- Ensure all other records are set to 'USER'
UPDATE users
SET user_roles = 'USER'
WHERE user_roles != 'ADMIN';

-- Commit the transaction
COMMIT;