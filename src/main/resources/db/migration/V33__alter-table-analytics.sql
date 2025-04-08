ALTER TABLE analytics 
    DROP FOREIGN KEY IF EXISTS fk_generic_analytics_validated_by,
    DROP FOREIGN KEY IF EXISTS fk_generic_analytics_user;
