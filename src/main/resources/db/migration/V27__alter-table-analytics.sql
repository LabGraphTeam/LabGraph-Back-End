ALTER TABLE analytics
    DROP CONSTRAINT IF EXISTS fk_generic_analytics_validated_by,
    DROP CONSTRAINT IF EXISTS fk_generic_analytics_user, 
    ADD CONSTRAINT FK_analytics_owner_user_id 
        FOREIGN KEY (owner_user_id) 
        REFERENCES users(id)
        ON DELETE SET NULL,
    ADD CONSTRAINT FK_analytics_validator_user_id
        FOREIGN KEY (validator_user_id) 
        REFERENCES users(id)
        ON DELETE SET NULL;