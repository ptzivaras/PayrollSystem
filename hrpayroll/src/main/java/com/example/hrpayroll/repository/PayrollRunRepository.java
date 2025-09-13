package com.example.hrpayroll.repository;

import com.example.hrpayroll.entity.PayrollRun;
import com.example.hrpayroll.entity.PayrollStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;

public interface PayrollRunRepository extends JpaRepository<PayrollRun, Long> {

    // Έλεγχος ύπαρξης run για συγκεκριμένη περίοδο & department
    boolean existsByPeriodAndDepartment_Id(LocalDate period, Long departmentId);

    // Για runs χωρίς department (company-wide)
    boolean existsByPeriodAndDepartmentIsNull(LocalDate period);

    Optional<PayrollRun> findByIdAndStatus(Long id, PayrollStatus status);

    @Query("""
           select pr
             from PayrollRun pr
            where (:departmentId is null or pr.department.id = :departmentId)
              and (:period is null or pr.period = :period)
           """)
    Page<PayrollRun> search(Long departmentId, LocalDate period, Pageable pageable);
}
