-- V3: Stored function that matches the code & schema
-- Name: calc_payroll_for_period(date, bigint)
-- Rules:
--  - Creates one payroll_run for the given period (first day of month recommended)
--  - Uses status 'PENDING' (enum payroll_status)
--  - Inserts payroll_item rows for all active employees (or by department if provided)
--  - Calculates gross/tax/net based on employee.base_salary
--  - Enforces uniqueness (V2 already has unique indexes)

-- Optional: remove any previous incompatible function
DO $$
BEGIN
  IF EXISTS (
    SELECT 1
    FROM pg_proc
    WHERE proname = 'generate_payroll_run'
      AND pg_function_is_visible(oid)
  ) THEN
    DROP FUNCTION IF EXISTS generate_payroll_run(date, bigint);
  END IF;
END$$;

CREATE OR REPLACE FUNCTION calc_payroll_for_period(
    p_period DATE,
    p_department_id BIGINT DEFAULT NULL
) RETURNS BIGINT
LANGUAGE plpgsql
AS $$
DECLARE
    v_period  DATE;
    v_run_id  BIGINT;
BEGIN
    -- normalize to first of month
    v_period := date_trunc('month', p_period)::date;

    -- prevent duplicates (V2 unique indexes will also enforce)
    IF p_department_id IS NULL THEN
        IF EXISTS (SELECT 1 FROM payroll_run
                    WHERE period = v_period AND department_id IS NULL) THEN
            RAISE EXCEPTION 'Payroll run for % (company-wide) already exists', v_period
              USING ERRCODE = 'unique_violation';
        END IF;
    ELSE
        IF EXISTS (SELECT 1 FROM payroll_run
                    WHERE period = v_period AND department_id = p_department_id) THEN
            RAISE EXCEPTION 'Payroll run for % (department %) already exists', v_period, p_department_id
              USING ERRCODE = 'unique_violation';
        END IF;
    END IF;

    -- create run as PENDING (enum payroll_status)
    INSERT INTO payroll_run(period, department_id, status, created_at)
    VALUES (v_period, p_department_id, 'PENDING', now())
    RETURNING id INTO v_run_id;

    -- insert items for each active employee in scope
    INSERT INTO payroll_item (payroll_run_id, employee_id, gross_amount, tax_amount, net_amount)
    SELECT
        v_run_id,
        e.id,
        e.base_salary AS gross_amount,
        ROUND(e.base_salary * 0.20, 2) AS tax_amount,
        e.base_salary - ROUND(e.base_salary * 0.20, 2) AS net_amount
    FROM employee e
    WHERE e.is_active = true
      AND (p_department_id IS NULL OR e.department_id = p_department_id);

    RETURN v_run_id;
END;
$$;
