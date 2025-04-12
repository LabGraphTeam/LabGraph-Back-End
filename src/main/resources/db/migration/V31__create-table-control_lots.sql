CREATE TABLE control_lots (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    id_user BIGINT NOT NULL,
    lot_code VARCHAR(50) UNIQUE NOT NULL,
    manufacture_date DATE NOT NULL,
    expiration_time DATE NOT NULL,
    CONSTRAINT fk_control_lots_user FOREIGN KEY (id_user) REFERENCES users(id)
);