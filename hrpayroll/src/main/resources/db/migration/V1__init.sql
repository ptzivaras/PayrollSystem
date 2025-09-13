-- Enum for payroll run status
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'payroll_status') THEN
    CREATE TYPE payroll_status AS ENUM ('PENDING', 'POSTED');
  END IF;
END$$;

-- Department
CREATE TABLE IF NOT EXISTS department (
  id           BIGSERIAL PRIMARY KEY,
  name         VARCHAR(120) NOT NULL,
  code         VARCHAR(32)  NOT NULL UNIQUE,
  created_at   TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- Employee
CREATE TABLE IF NOT EXISTS employee (
  id             BIGSERIAL PRIMARY KEY,
  first_name     VARCHAR(80)  NOT NULL,
  last_name      VARCHAR(80)  NOT NULL,
  email          VARCHAR(160) NOT NULL UNIQUE,
  department_id  BIGINT       REFERENCES department(id) ON UPDATE RESTRICT ON DELETE RESTRICT,
  base_salary    NUMERIC(12,2) NOT NULL CHECK (base_salary >= 0),
  hire_date      DATE          NOT NULL,
  is_active      BOOLEAN       NOT NULL DEFAULT true,
  created_at     TIMESTAMPTZ   NOT NULL DEFAULT now()
);

-- Payroll Run (per month, optionally per department)
CREATE TABLE IF NOT EXISTS payroll_run (
  id             BIGSERIAL PRIMARY KEY,
  period         DATE           NOT NULL, -- store first day of month (YYYY-MM-01)
  department_id  BIGINT         NULL REFERENCES department(id) ON UPDATE RESTRICT ON DELETE RESTRICT,
  status         payroll_status NOT NULL DEFAULT 'PENDING',
  created_at     TIMESTAMPTZ    NOT NULL DEFAULT now()
);

-- Payroll Item (one per employee per run)
CREATE TABLE IF NOT EXISTS payroll_item (
  id             BIGSERIAL PRIMARY KEY,
  payroll_run_id BIGINT NOT NULL REFERENCES payroll_run(id) ON UPDATE CASCADE ON DELETE CASCADE,
  employee_id    BIGINT NOT NULL REFERENCES employee(id) ON UPDATE RESTRICT ON DELETE RESTRICT,
  gross_amount   NUMERIC(12,2) NOT NULL CHECK (gross_amount >= 0),
  tax_amount     NUMERIC(12,2) NOT NULL CHECK (tax_amount >= 0),
  net_amount     NUMERIC(12,2) NOT NULL CHECK (net_amount >= 0),
  notes          VARCHAR(255)
);

-- Helpful indexes
CREATE INDEX IF NOT EXISTS idx_employee_last_name ON employee(last_name);
CREATE INDEX IF NOT EXISTS idx_payroll_run_period ON payroll_run(period);
CREATE INDEX IF NOT EXISTS idx_payroll_item_emp_run ON payroll_item(employee_id, payroll_run_id);

-- Uniqueness: avoid duplicate payroll item for same employee within same run
ALTER TABLE payroll_item
  ADD CONSTRAINT uq_payroll_item_emp_run UNIQUE (employee_id, payroll_run_id);
