CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    role VARCHAR(50) NOT NULL
);

INSERT INTO users (email, password, first_name, last_name, phone_number, role) VALUES 
('admin@flight.com', 'ruhi123', 'Kaya Furkan', 'İPEKCİ', '5062564032', 'ADMIN'),
('ahmet@flight.com', '!voco*', 'Ahmet', 'Yilmaz', '5554445566', 'PASSENGER')
ON CONFLICT (email) DO NOTHING;