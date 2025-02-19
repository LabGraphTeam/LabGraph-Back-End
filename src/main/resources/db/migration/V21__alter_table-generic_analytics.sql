ALTER TABLE generic_analytics
ADD COLUMN validated_by_id BIGINT,
ADD CONSTRAINT fk_generic_analytics_validated_by 
    FOREIGN KEY (validated_by_id) 
    REFERENCES users(id);