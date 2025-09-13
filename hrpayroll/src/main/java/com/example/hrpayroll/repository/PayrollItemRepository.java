package com.example.hrpayroll.repository;

import com.example.hrpayroll.entity.PayrollItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface PayrollItemRepository extends JpaRepository<PayrollItem, Long> {

    Page<PayrollItem> findAllByPayrollRun_Id(Long payrollRunId, Pageable pageable);

    @Query("""
           select pi
             from PayrollItem pi
             join pi.payrollRun pr
            where pi.employee.id = :employeeId
              and (:period is null or pr.period = :period)
           """)
    Page<PayrollItem> findByEmployeeAndPeriod(Long employeeId, LocalDate period, Pageable pageable);

    @Query("""
           select coalesce(sum(pi.grossAmount), 0)
             from PayrollItem pi
            where pi.payrollRun.id = :runId
           """)
    java.math.BigDecimal sumGrossByRun(Long runId);

    @Query("""
           select coalesce(sum(pi.netAmount), 0)
             from PayrollItem pi
            where pi.payrollRun.id = :runId
           """)
    java.math.BigDecimal sumNetByRun(Long runId);
}
