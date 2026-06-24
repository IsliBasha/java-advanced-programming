-- Sample travel bookings seeded on startup (ddl-auto=create-drop recreates the schema each run)
INSERT INTO bookings (customer_name, destination, departure_date, return_date, price, status, number_of_travellers) VALUES
('Alice Johnson', 'Paris',     '2026-07-01', '2026-07-10', 1250.00, 'CONFIRMED', 2),
('Bob Smith',     'Tokyo',     '2026-08-15', '2026-08-28', 3400.50, 'PENDING',   1),
('Carol White',   'Barcelona', '2026-09-05', '2026-09-12',  980.00, 'CONFIRMED', 3),
('David Brown',   'Rome',      '2026-07-20', '2026-07-27', 1100.00, 'CANCELLED', 2),
('Eva Green',     'Cairo',     '2026-10-01', '2026-10-09', 1600.75, 'PENDING',   4),
('Frank Miller',  'Barcelona', '2026-11-11', '2026-11-18', 1050.00, 'CONFIRMED', 2);
