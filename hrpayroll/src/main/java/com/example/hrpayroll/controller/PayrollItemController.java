package com.example.hrpayroll.controller;

import com.example.hrpayroll.dto.PayrollItemDto;
import com.example.hrpayroll.service.PayrollItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/payroll/items")
public class PayrollItemController {

    private final PayrollItemService service;

    public PayrollItemController(PayrollItemService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PayrollItemDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<PayrollItemDto>> list(
            @RequestParam(required = false) Long runId,
            @RequestParam(required = false) Long employeeId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        if (runId != null) {
            return ResponseEntity.ok(service.listByRun(runId, pageable));
        }
        if (employeeId != null) {
            return ResponseEntity.ok(service.listByEmployee(employeeId, period, pageable));
        }
        // Αν δεν έχει δοθεί φίλτρο, επιστρέφουμε 400 για σαφήνεια
        return ResponseEntity.badRequest().build();
    }
}
