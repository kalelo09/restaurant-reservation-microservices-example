CREATE SEQUENCE IF NOT EXISTS reservation_id_sequence START WITH 1 INCREMENT BY 1;

CREATE TABLE reservation (
                             id BIGINT PRIMARY KEY,
                             id_restaurant BIGINT NOT NULL,
                             customer_name VARCHAR(255) NOT NULL,
                             reservation_date DATE NOT NULL,
                             canceled BOOLEAN DEFAULT FALSE
);