-- Seed departments
INSERT INTO department (name, code)
VALUES ('Engineering', 'ENG')
ON CONFLICT (code) DO NOTHING;

INSERT INTO department (name, code)
VALUES ('HR', 'HR')
ON CONFLICT (code) DO NOTHING;

-- Seed employees
WITH eng AS (
  SELECT id FROM department WHERE code = 'ENG'
),
hr AS (
  SELECT id FROM department WHERE code = 'HR'
)
INSERT INTO employee (first_name, last_name, email, department_id, base_salary, hire_date, is_active)
VALUES
  ('Ada',   'Lovelace', 'ada@example.com', (SELECT id FROM eng), 1600.00, CURRENT_DATE, true),
  ('Grace', 'Hopper',   'grace@example.com', (SELECT id FROM eng), 1800.00, CURRENT_DATE, true),
  ('Alan',  'Turing',   'alan@example.com', (SELECT id FROM hr),  1500.00, CURRENT_DATE, true)
ON CONFLICT (email) DO NOTHING;
