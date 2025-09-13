package com.example.hrpayroll.it;

import com.example.hrpayroll.dto.EmployeeDto;
import com.example.hrpayroll.it.containers.PostgresTestContainer;
import com.example.hrpayroll.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Ελέγχει ότι δεν μπορείς να δημιουργήσεις δεύτερο employee με ίδιο email.
 * Εδώ το πιάνουμε στο service (existsByEmail) αλλά και η DB έχει unique constraint.
 */
@SpringBootTest
class EmployeeUniquenessIT extends PostgresTestContainer {

    @Autowired
    private EmployeeService employeeService;

    @Test
    void duplicate_email_should_fail() {
        EmployeeDto e1 = new EmployeeDto();
        e1.setFirstName("John");
        e1.setLastName("Doe");
        e1.setEmail("john@company.test");
        e1.setBaseSalary(new BigDecimal("1200.00"));
        e1.setHireDate(LocalDate.now());
        e1.setActive(true);
        employeeService.create(e1);

        EmployeeDto e2 = new EmployeeDto();
        e2.setFirstName("Johnny");
        e2.setLastName("Doe");
        e2.setEmail("john@company.test");
        e2.setBaseSalary(new BigDecimal("1300.00"));
        e2.setHireDate(LocalDate.now());
        e2.setActive(true);

        Exception ex = assertThrows(RuntimeException.class, () -> employeeService.create(e2));
        assertTrue(ex.getMessage().toLowerCase().contains("email"), "Πρέπει να μπλοκάρει το ίδιο email");
    }
}
