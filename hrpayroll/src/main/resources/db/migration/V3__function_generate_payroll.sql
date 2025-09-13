-- V3: Stored procedure to generate payroll run and items

CREATE OR REPLACE FUNCTION generate_payroll_run(
    p_period DATE,
    p_department_id BIGINT DEFAULT NULL
) RETURNS BIGINT
LANGUAGE plpgsql
AS $$
DECLARE
    v_run_id BIGINT;
BEGIN
    -- Prevent duplicate runs for same period & department/null
    IF p_department_id IS NULL THEN
        IF EXISTS (SELECT 1 FROM payroll_run
                   WHERE period = p_period AND department_id IS NULL) THEN
            RAISE EXCEPTION 'Payroll run for % (company-wide) already exists', p_period;
        END IF;
    ELSE
        IF EXISTS (SELECT 1 FROM payroll_run
                   WHERE period = p_period AND department_id = p_department_id) THEN
            RAISE EXCEPTION 'Payroll run for % (department %) already exists', p_period, p_department_id;
        END IF;
    END IF;

    -- Create payroll run in DRAFT status
    INSERT INTO payroll_run(period, department_id, status, created_at)
    VALUES (p_period, p_department_id, 'DRAFT', now())
    RETURNING id INTO v_run_id;

    -- Insert payroll items for each employee
    INSERT INTO payroll_item(employee_id, payroll_run_id, gross_amount, net_amount)
    SELECT e.id,
           v_run_id,
           e.salary,
           ROUND(e.salary * 0.80, 2)  -- simple 20% deduction for example
    FROM employee e
    WHERE (p_department_id IS NULL OR e.department_id = p_department_id);

    RETURN v_run_id;
END;
$$;
