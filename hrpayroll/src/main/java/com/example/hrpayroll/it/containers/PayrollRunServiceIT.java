package com.example.hrpayroll.it;

import com.example.hrpayroll.dto.DepartmentDto;
import com.example.hrpayroll.dto.PayrollRunDto;
import com.example.hrpayroll.service.DepartmentService;
import com.example.hrpayroll.service.PayrollRunService;
import com.example.hrpayroll.it.containers.PostgresTestContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PayrollRunServiceIT extends PostgresTestContainer {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private PayrollRunService payrollRunService;

    @Test
    void createRun_companyWide_thenPost() {
        // Ensure we can create a company-wide run for a period
        LocalDate period = LocalDate.of(2025, 1, 1);
        PayrollRunDto run = payrollRunService.createRun(period, null);
        assertNotNull(run.getId());
        assertEquals(period, run.getPeriod());

        // Posting it should succeed
        PayrollRunDto posted = payrollRunService.postRun(run.getId());
        assertEquals("POSTED", posted.getStatus());
    }

    @Test
    void createRun_forDepartment_onlyOncePerPeriod() {
        // Create a department first
        DepartmentDto dep = new DepartmentDto();
        dep.setName("Engineering");
        dep.setCode("ENG");
        DepartmentDto saved = departmentService.create(dep);

        LocalDate period = LocalDate.of(2025, 2, 1);

        PayrollRunDto first = payrollRunService.createRun(period, saved.getId());
        assertNotNull(first.getId());

        // Second attempt for same period & department should fail
        Exception ex = assertThrows(RuntimeException.class,
                () -> payrollRunService.createRun(period, saved.getId()));
        assertTrue(ex.getMessage().toLowerCase().contains("already"), "Should reject duplicate run");
    }
}
