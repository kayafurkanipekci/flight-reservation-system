CREATE TABLE reservations (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    airplane_id BIGINT NOT NULL,
    departure_airport_id BIGINT NOT NULL,
    arrival_airport_id BIGINT NOT NULL,
    flight_date TIMESTAMP NOT NULL,
    seat_number VARCHAR(10) NOT NULL,
    status VARCHAR(20) NOT NULL,
    
    CONSTRAINT fk_reservation_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_reservation_airplane FOREIGN KEY (airplane_id) REFERENCES airplanes(id),
    CONSTRAINT fk_reservation_departure FOREIGN KEY (departure_airport_id) REFERENCES airports(id),
    CONSTRAINT fk_reservation_arrival FOREIGN KEY (arrival_airport_id) REFERENCES airports(id)
);