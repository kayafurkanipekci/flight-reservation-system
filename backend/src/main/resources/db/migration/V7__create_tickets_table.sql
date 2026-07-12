CREATE TABLE tickets (
    id BIGSERIAL PRIMARY KEY,
    reservation_id BIGINT NOT NULL,
    flight_id BIGINT NOT NULL,
    seat_number VARCHAR(10) NOT NULL,
    segment_order INTEGER NOT NULL,

    CONSTRAINT fk_ticket_reservation FOREIGN KEY (reservation_id) REFERENCES reservations(id),
    CONSTRAINT fk_ticket_flight FOREIGN KEY (flight_id) REFERENCES flights(id),
    CONSTRAINT uq_ticket_flight_seat UNIQUE (flight_id, seat_number)
);