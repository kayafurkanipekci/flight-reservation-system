CREATE TABLE flights (
    id BIGSERIAL PRIMARY KEY,
    flight_number VARCHAR(10) NOT NULL UNIQUE,
    airplane_id BIGINT NOT NULL,
    departure_airport_id BIGINT NOT NULL,
    arrival_airport_id BIGINT NOT NULL,
    departure_time TIMESTAMP NOT NULL,
    arrival_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,

    CONSTRAINT fk_flight_airplane FOREIGN KEY (airplane_id) REFERENCES airplanes(id),
    CONSTRAINT fk_flight_departure_airport FOREIGN KEY (departure_airport_id) REFERENCES airports(id),
    CONSTRAINT fk_flight_arrival_airport FOREIGN KEY (arrival_airport_id) REFERENCES airports(id)
);