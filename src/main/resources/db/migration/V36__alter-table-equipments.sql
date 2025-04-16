ALTER TABLE control_lots
    ADD COLUMN id_equipment INTEGER,
    ADD CONSTRAINT fk_equipments_control_lots FOREIGN KEY (id_equipment) REFERENCES equipments(id);