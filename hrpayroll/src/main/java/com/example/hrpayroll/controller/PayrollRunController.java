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

    @PostMapping
    public ResponseEntity<PayrollRunDto> createRun(
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period,
            @RequestParam(required = false) Long departmentId,
            UriComponentsBuilder uriBuilder
    ) {
        // normalize to first-of-month
        LocalDate first = period.withDayOfMonth(1);
        PayrollRunDto created = service.createRun(first, departmentId);
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
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        // normalize if present
        LocalDate first = (period == null) ? null : period.withDayOfMonth(1);
        return ResponseEntity.ok(service.list(departmentId, first, pageable));
    }
}
