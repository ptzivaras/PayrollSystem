-- Seed one company-wide payroll run for 2025-08-01 and items for all employees.
-- Idempotent: safe to re-run.

WITH existing AS (
  SELECT id
  FROM payroll_run
  WHERE period = DATE '2025-08-01' AND department_id IS NULL
),
new_run AS (
  INSERT INTO payroll_run (period, department_id, status, created_at)
  SELECT DATE '2025-08-01', NULL, 'PENDING', now()
  WHERE NOT EXISTS (SELECT 1 FROM existing)
  RETURNING id
),
run AS (
  SELECT id FROM existing
  UNION ALL
  SELECT id FROM new_run
)
INSERT INTO payroll_item (
  payroll_run_id, employee_id, gross_amount, tax_amount, net_amount, notes
)
SELECT
  r.id,
  e.id,
  e.base_salary,
  ROUND(e.base_salary * 0.20, 2) AS tax_amount,
  ROUND(e.base_salary * 0.80, 2) AS net_amount,
  'Seeded for 2025-08'
FROM employee e
CROSS JOIN run r
ON CONFLICT (employee_id, payroll_run_id) DO NOTHING;
