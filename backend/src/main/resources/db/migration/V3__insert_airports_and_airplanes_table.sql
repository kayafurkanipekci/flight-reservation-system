INSERT INTO airports (name, iata_code, city, country) VALUES 
('Istanbul Airport', 'IST', 'Istanbul', 'Turkiye'),
('Ankara Esenboga Airport', 'ESB', 'Ankara', 'Turkiye'),
('Izmir Adnan Menderes Airport', 'ADB', 'Izmir', 'Turkiye')
ON CONFLICT (iata_code) DO NOTHING;


INSERT INTO airplanes (model, tail_number, capacity, airline) VALUES 
('Boeing 737-800', 'TC-JFY', 189, 'Turkish Airlines'),
('Airbus A320-200', 'TC-NBG', 186, 'Pegasus Airlines'),
('Boeing 777-300ER', 'TC-LJA', 349, 'Turkish Airlines')
ON CONFLICT (tail_number) DO NOTHING;