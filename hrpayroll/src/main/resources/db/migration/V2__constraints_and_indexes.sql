-- V2: Additional constraints & indexes for optimization and uniqueness

-- Unique payroll run per month per department (when department_id is NOT NULL)
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_indexes
      WHERE schemaname = 'public'
        AND indexname = 'uq_payroll_run_period_dept_notnull'
  ) THEN
    CREATE UNIQUE INDEX uq_payroll_run_period_dept_notnull
      ON payroll_run (period, department_id)
      WHERE department_id IS NOT NULL;
  END IF;
END$$;

-- Unique company-wide payroll run per month (when department_id IS NULL)
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_indexes
      WHERE schemaname = 'public'
        AND indexname = 'uq_payroll_run_period_null_dept'
  ) THEN
    CREATE UNIQUE INDEX uq_payroll_run_period_null_dept
      ON payroll_run (period)
      WHERE department_id IS NULL;
  END IF;
END$$;

-- Helpful composite index for listing/searching employees by (department_id, last_name)
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_indexes
      WHERE schemaname = 'public'
        AND indexname = 'idx_employee_dept_lastname'
  ) THEN
    CREATE INDEX idx_employee_dept_lastname
      ON employee (department_id, last_name);
  END IF;
END$$;

-- Helpful composite index for listing/searching runs by (department_id, period, status)
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_indexes
      WHERE schemaname = 'public'
        AND indexname = 'idx_payroll_run_dept_period_status'
  ) THEN
    CREATE INDEX idx_payroll_run_dept_period_status
      ON payroll_run (department_id, period, status);
  END IF;
END$$;

-- Composite index on payroll_item for aggregations by run (already have employee_id, payroll_run_id; add by run_id alone if missing)
DO $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM pg_indexes
      WHERE schemaname = 'public'
        AND indexname = 'idx_payroll_item_run'
  ) THEN
    CREATE INDEX idx_payroll_item_run
      ON payroll_item (payroll_run_id);
  END IF;
END$$;
