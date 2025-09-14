package com.example.hrpayroll.controller;

import com.example.hrpayroll.dto.EmployeeDto;
import com.example.hrpayroll.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<EmployeeDto> create(@Valid @RequestBody EmployeeDto dto,
                                              UriComponentsBuilder uriBuilder) {
        EmployeeDto created = service.create(dto);
        return ResponseEntity
                .created(uriBuilder.path("/api/employees/{id}").build(created.getId()))
                .body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDto> update(@PathVariable Long id,
                                              @Valid @RequestBody EmployeeDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<EmployeeDto>> search(@RequestParam(required = false) String q,
                                                    @RequestParam(required = false) Long departmentId,
                                                    // IMPORTANT: no default sort here; native query does ORDER BY
                                                    @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(service.search(q, departmentId, pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
