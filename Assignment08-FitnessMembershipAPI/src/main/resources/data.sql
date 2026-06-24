-- Sample members seeded on startup (ddl-auto=create-drop recreates the schema each run)
INSERT INTO members (full_name, email, membership_type, active, join_date, monthly_fee) VALUES
('Liam O''Connor',  'liam.oconnor@example.com',   'PREMIUM',  TRUE,  '2025-01-15', 59.99),
('Sofia Marin',     'sofia.marin@example.com',    'STANDARD', TRUE,  '2025-03-02', 39.99),
('Noah Patel',      'noah.patel@example.com',     'BASIC',    TRUE,  '2025-05-20', 24.99),
('Emma Rossi',      'emma.rossi@example.com',     'PREMIUM',  FALSE, '2024-11-10', 59.99),
('Oliver Schmidt',  'oliver.schmidt@example.com', 'STANDARD', FALSE, '2024-09-01', 39.99),
('Ava Nakamura',    'ava.nakamura@example.com',   'BASIC',    TRUE,  '2025-06-01', 24.99);
