ALTER TABLE analytics
MODIFY COLUMN control_rules varchar(15) NOT NULL,
MODIFY COLUMN measurement_unit varchar(15) NOT NULL,
MODIFY COLUMN description varchar(50) NOT NULL;