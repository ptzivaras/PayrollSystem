package com.example.hrpayroll.controller;

import com.example.hrpayroll.dto.PayrollRunDto;
import com.example.hrpayroll.service.PayrollRunService;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/payroll/runs")
public class PayrollRunController {

    private final PayrollRunService service;

    public PayrollRunController(PayrollRunService service) {
        this.service = service;
    }

    /**
     * Δημιουργεί run για την περίοδο (π.χ. ?period=2025-09-01) και προαιρετικά departmentId.
     * Καλεί την stored procedure στο service.
     */
    @PostMapping
    public ResponseEntity<PayrollRunDto> createRun(
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period,
            @RequestParam(required = false) Long departmentId,
            UriComponentsBuilder uriBuilder
    ) {
        PayrollRunDto created = service.createRun(period, departmentId);
        return ResponseEntity
                .created(uriBuilder.path("/api/payroll/runs/{id}").build(created.getId()))
                .body(created);
    }

    @PostMapping("/{id}/post")
    public ResponseEntity<PayrollRunDto> post(@PathVariable Long id) {
        return ResponseEntity.ok(service.postRun(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PayrollRunDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<PayrollRunDto>> list(
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable
    ) {
        return ResponseEntity.ok(service.list(departmentId, period, pageable));
    }
}
