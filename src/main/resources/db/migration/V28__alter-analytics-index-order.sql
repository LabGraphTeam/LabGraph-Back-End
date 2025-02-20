DROP INDEX IF EXISTS generic_analytics_name_level_date_IDX ON analytics;
DROP INDEX IF EXISTS integra_400_name_IDX ON analytics;

CREATE INDEX analytics_test_control_date_idx 
ON analytics (
    test_name,
    control_level,
    measurement_date DESC
);