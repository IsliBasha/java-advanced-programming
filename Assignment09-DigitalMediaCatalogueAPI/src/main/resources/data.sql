-- Sample catalogue items seeded on startup (ddl-auto=create-drop recreates the schema each run)
INSERT INTO media_items (title, type, genre, release_year, available) VALUES
('Inception',              'MOVIE', 'Science Fiction', 2010, TRUE),
('The Dark Knight',        'MOVIE', 'Action',          2008, FALSE),
('Random Access Memories', 'MUSIC', 'Electronic',      2013, TRUE),
('Clean Code',             'EBOOK', 'Programming',     2008, TRUE),
('The Pragmatic Programmer','EBOOK','Programming',     1999, FALSE),
('The Witcher 3',          'GAME',  'RPG',             2015, TRUE),
('Hades',                  'GAME',  'Roguelike',       2020, TRUE);
