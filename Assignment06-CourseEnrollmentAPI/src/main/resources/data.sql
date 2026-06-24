-- Assignment 06: Course Enrolment Management API – Sample seed data
INSERT INTO enrollments (student_id, student_name, course_id, course_name, status, completion_percentage, enrolled_on)
VALUES
  ('S001', 'Alice Johnson',  'CS101', 'Java Programming',     'ACTIVE',    78.5, '2024-01-15'),
  ('S002', 'Bob Martinez',   'CS101', 'Java Programming',     'ACTIVE',    42.0, '2024-01-20'),
  ('S003', 'Clara Schmidt',  'CS202', 'Data Structures',      'ACTIVE',    95.0, '2024-02-01'),
  ('S001', 'Alice Johnson',  'CS202', 'Data Structures',      'COMPLETED', 100.0,'2024-02-05'),
  ('S004', 'David Okonkwo',  'CS303', 'Algorithms',           'DROPPED',   30.0, '2024-02-10'),
  ('S005', 'Eva Lindström',  'CS303', 'Algorithms',           'ACTIVE',    55.0, '2024-03-01');
