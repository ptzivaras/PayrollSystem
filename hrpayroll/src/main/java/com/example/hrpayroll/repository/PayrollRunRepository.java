package com.example.hrpayroll.repository;

import com.example.hrpayroll.entity.PayrollRun;
import com.example.hrpayroll.entity.PayrollStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface PayrollRunRepository extends JpaRepository<PayrollRun, Long> {

    // existence checks used before creating runs
    boolean existsByPeriodAndDepartment_Id(LocalDate period, Long departmentId);
    boolean existsByPeriodAndDepartmentIsNull(LocalDate period);

    Optional<PayrollRun> findByIdAndStatus(Long id, PayrollStatus status);

    // Branch-friendly queries (no nullable params inside a single JPQL)
    Page<PayrollRun> findByPeriod(LocalDate period, Pageable pageable);
    Page<PayrollRun> findByDepartment_Id(Long departmentId, Pageable pageable);
    Page<PayrollRun> findByDepartment_IdAndPeriod(Long departmentId, LocalDate period, Pageable pageable);
}
