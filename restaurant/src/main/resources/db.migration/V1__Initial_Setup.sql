CREATE SEQUENCE IF NOT EXISTS restaurant_id_sequence START WITH 1 INCREMENT BY 1;

CREATE TABLE restaurant (
                            id BIGINT PRIMARY KEY,
                            name VARCHAR(255) NOT NULL UNIQUE,
                            address VARCHAR(255) NOT NULL,
                            number_table BIGINT NOT NULL CHECK (number_table >= 1),
                            number_table_reserved BIGINT NOT NULL CHECK (number_table_reserved >= 0)
);