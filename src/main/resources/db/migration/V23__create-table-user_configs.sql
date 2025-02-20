CREATE TABLE user_configs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT UNIQUE NOT NULL,
    default_chart_type VARCHAR(25) NOT NULL DEFAULT 'SINGLE_LINE',
    default_rules VARCHAR(25) NOT NULL DEFAULT 'RULE_1_3S',
    theme_preference VARCHAR(25) NOT NULL DEFAULT 'LIGHT',
    auto_calculate_sd BOOLEAN NOT NULL DEFAULT false,
    decimal_places INTEGER NOT NULL DEFAULT 2,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_config_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Insert default configs for existing users
INSERT INTO user_configs (user_id, default_chart_type, default_rules, theme_preference, auto_calculate_sd, decimal_places)
SELECT 
    id,
    'SINGLE_LINE',
    'RULE_1_3S',
    'LIGHT',
    true,
    2
FROM users
WHERE id NOT IN (SELECT user_id FROM user_configs);
