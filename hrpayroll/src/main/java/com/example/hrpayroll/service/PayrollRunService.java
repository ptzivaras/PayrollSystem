package com.example.hrpayroll.service;

import com.example.hrpayroll.dto.PayrollRunDto;
import com.example.hrpayroll.entity.PayrollRun;
import com.example.hrpayroll.entity.PayrollStatus;
import com.example.hrpayroll.exception.BusinessException;
import com.example.hrpayroll.exception.ResourceNotFoundException;
import com.example.hrpayroll.mapper.PayrollRunMapper;
import com.example.hrpayroll.repository.DepartmentRepository;
import com.example.hrpayroll.repository.PayrollRunRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class PayrollRunService {

    private final PayrollRunRepository runRepository;
    private final DepartmentRepository departmentRepository;
    private final PayrollRunMapper mapper;
    private final JdbcTemplate jdbcTemplate;

    public PayrollRunService(PayrollRunRepository runRepository,
                             DepartmentRepository departmentRepository,
                             PayrollRunMapper mapper,
                             JdbcTemplate jdbcTemplate) {
        this.runRepository = runRepository;
        this.departmentRepository = departmentRepository;
        this.mapper = mapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Δημιουργεί νέο payroll run για συγκεκριμένη περίοδο (π.χ. 2025-09-01).
     * Καλεί την stored procedure calc_payroll_for_period(period, departmentId).
     */
    @Transactional
    public PayrollRunDto createRun(LocalDate period, Long departmentId) {
        LocalDate firstOfMonth = period.withDayOfMonth(1);

        // Guard: μη δημιουργήσεις δεύτερο run για την ίδια περίοδο+department
        boolean exists = (departmentId == null)
                ? runRepository.existsByPeriodAndDepartmentIsNull(firstOfMonth)
                : runRepository.existsByPeriodAndDepartment_Id(firstOfMonth, departmentId);

        if (exists) {
            throw new BusinessException("Payroll run already exists for period " + firstOfMonth +
                    (departmentId == null ? "" : (" and department " + departmentId)));
        }

        if (departmentId != null) {
            departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + departmentId));
        }

        // Κλήση stored procedure (PL/pgSQL)
        try {
            if (departmentId == null) {
                jdbcTemplate.update("SELECT calc_payroll_for_period(?::date, NULL)", firstOfMonth);
            } else {
                jdbcTemplate.update("SELECT calc_payroll_for_period(?::date, ?)", firstOfMonth, departmentId);
            }
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException("Failed to create payroll run: " + ex.getMostSpecificCause().getMessage());
        }

        // Επιστροφή του run που μόλις δημιουργήθηκε (τελευταίο PENDING για period/department)
        Page<PayrollRun> page = runRepository.search(departmentId, firstOfMonth, Pageable.ofSize(1));
        PayrollRun run = page.stream().findFirst()
                .orElseThrow(() -> new BusinessException("Payroll run creation did not persist as expected."));
        return mapper.toDto(run);
    }

    @Transactional
    public PayrollRunDto postRun(Long runId) {
        PayrollRun run = runRepository.findById(runId)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll run not found: " + runId));

        if (run.getStatus() == PayrollStatus.POSTED) {
            throw new BusinessException("Payroll run already posted.");
        }
        run.setStatus(PayrollStatus.POSTED);
        return mapper.toDto(runRepository.save(run));
    }

    @Transactional(readOnly = true)
    public Page<PayrollRunDto> list(Long departmentId, LocalDate period, Pageable pageable) {
        return runRepository.search(departmentId, period, pageable).map(mapper::toDto);
    }

    @Transactional(readOnly = true)
    public PayrollRunDto get(Long id) {
        PayrollRun run = runRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll run not found: " + id));
        return mapper.toDto(run);
    }
}
