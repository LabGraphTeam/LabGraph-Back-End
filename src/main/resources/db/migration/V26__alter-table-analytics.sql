ALTER TABLE analytics
    CHANGE user_id owner_user_id BIGINT,
    CHANGE validated_by_id validator_user_id BIGINT,
    
    CHANGE date measurement_date DATETIME NOT NULL,
    
    CHANGE level_lot control_level_lot VARCHAR(25),
    CHANGE test_lot reagent_lot VARCHAR(25),
    
    CHANGE name test_name VARCHAR(25) NOT NULL,
    CHANGE level control_level VARCHAR(25) NOT NULL,
    
    CHANGE value measurement_value DOUBLE NOT NULL,
    CHANGE mean target_mean DOUBLE NOT NULL,
    CHANGE sd standard_deviation DOUBLE NOT NULL,
    
    CHANGE rules control_rules VARCHAR(100) NOT NULL,
    MODIFY description VARCHAR(100) NOT NULL,
    CHANGE unit_value measurement_unit  VARCHAR(25) NOT NULL;
;