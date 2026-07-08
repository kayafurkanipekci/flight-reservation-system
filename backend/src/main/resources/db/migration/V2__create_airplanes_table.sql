CREATE TABLE airplanes (
    id BIGSERIAL PRIMARY KEY,
    model VARCHAR(100) NOT NULL,
    tail_number VARCHAR(20) NOT NULL UNIQUE,
    capacity INTEGER NOT NULL CHECK (capacity > 0),
    airline VARCHAR(100) NOT NULL
);