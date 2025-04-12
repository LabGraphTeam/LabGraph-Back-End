ALTER TABLE analytics 
    ADD COLUMN equipment_id INTEGER,
    ADD CONSTRAINT fk_analytics_equipment FOREIGN KEY (equipment_id) REFERENCES equipments(id) ON DELETE SET NULL ON UPDATE SET NULL;