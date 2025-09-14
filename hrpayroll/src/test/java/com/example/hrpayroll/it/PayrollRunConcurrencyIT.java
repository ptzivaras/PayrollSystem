package com.example.hrpayroll.it;

import com.example.hrpayroll.dto.PayrollRunDto;
import com.example.hrpayroll.it.containers.PostgresTestContainer;
import com.example.hrpayroll.service.PayrollRunService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
//import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Ελέγχει ότι δύο ταυτόχρονες προσπάθειες δημιουργίας run για την ίδια περίοδο
 * δεν «περνάνε» και οι δύο. Μία επιτυγχάνει, η άλλη αποτυγχάνει λόγω uniqueness
 * (είτε από service guard είτε από DB unique index).
 */
@SpringBootTest
class PayrollRunConcurrencyIT extends PostgresTestContainer {

    @Autowired
    private PayrollRunService payrollRunService;

    @Test
    void concurrent_create_same_period_all_company_one_should_fail() throws Exception {
        LocalDate period = LocalDate.of(2025, 3, 1);

        Callable<PayrollRunDto> task = () -> payrollRunService.createRun(period, null);

        int threads = 2;
        ExecutorService exec = Executors.newFixedThreadPool(threads);
        List<Future<PayrollRunDto>> futures = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            futures.add(exec.submit(task));
        }
        exec.shutdown();
        //exec.awaitTermination(30, TimeUnit.SECONDS);

        int success = 0;
        int failures = 0;

        for (Future<PayrollRunDto> f : futures) {
            try {
                PayrollRunDto dto = f.get();
                assertNotNull(dto.getId());
                success++;
            } catch (ExecutionException ex) {
                // περιμένουμε BusinessException ή DataIntegrityViolationException wrapped
                failures++;
            }
        }
        assertEquals(1, success, "Μόνο ένα run πρέπει να δημιουργηθεί");
        assertEquals(1, failures, "Η δεύτερη παράλληλη προσπάθεια πρέπει να αποτύχει");
    }
}
