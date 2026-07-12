CREATE TABLE reservations (
    id BIGSERIAL PRIMARY KEY,
    passenger_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,

    CONSTRAINT fk_reservation_passenger FOREIGN KEY (passenger_id) REFERENCES users(id)
);