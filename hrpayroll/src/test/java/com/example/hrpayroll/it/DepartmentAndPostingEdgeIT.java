package com.example.hrpayroll.it;

import com.example.hrpayroll.dto.DepartmentDto;
import com.example.hrpayroll.dto.PayrollRunDto;
import com.example.hrpayroll.it.containers.PostgresTestContainer;
import com.example.hrpayroll.service.DepartmentService;
import com.example.hrpayroll.service.PayrollItemService;
import com.example.hrpayroll.service.PayrollRunService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Edge cases:
 * - Duplicate department code -> BusinessException
 * - Posting ήδη POSTED run -> BusinessException
 * - Δημιουργία run παράγει payroll items (> 0)
 */
@SpringBootTest
class DepartmentAndPostingEdgeIT extends PostgresTestContainer {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private PayrollRunService payrollRunService;

    @Autowired
    private PayrollItemService payrollItemService;

    @Test
    void duplicate_department_code_should_fail() {
        DepartmentDto dep = new DepartmentDto();
        dep.setName("Finance");
        dep.setCode("FIN");
        departmentService.create(dep);

        DepartmentDto dup = new DepartmentDto();
        dup.setName("Finance 2");
        dup.setCode("FIN");

        Exception ex = assertThrows(RuntimeException.class, () -> departmentService.create(dup));
        assertTrue(ex.getMessage().toLowerCase().contains("code"), "Πρέπει να αναφέρει το code ως διπλό");
    }

    @Test
    void posting_already_posted_run_should_fail() {
        LocalDate period = LocalDate.of(2025, 4, 1);
        PayrollRunDto run = payrollRunService.createRun(period, null);
        PayrollRunDto posted = payrollRunService.postRun(run.getId());
        assertEquals("POSTED", posted.getStatus());

        Exception ex = assertThrows(RuntimeException.class,
                () -> payrollRunService.postRun(run.getId()));
        assertTrue(ex.getMessage().toLowerCase().contains("already posted"));
    }

    @Test
    void create_run_should_generate_items() {
        LocalDate period = LocalDate.of(2025, 5, 1);
        PayrollRunDto run = payrollRunService.createRun(period, null);

        var page = payrollItemService.listByRun(run.getId(), PageRequest.of(0, 50));
        assertTrue(page.getTotalElements() > 0, "Πρέπει να δημιουργούνται payroll items για ενεργούς employees");
    }
}
