package com.example.hrpayroll.controller;

import com.example.hrpayroll.dto.DepartmentDto;
import com.example.hrpayroll.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService service;

    public DepartmentController(DepartmentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<DepartmentDto> create(@Valid @RequestBody DepartmentDto dto,
                                                UriComponentsBuilder uriBuilder) {
        DepartmentDto created = service.create(dto);
        return ResponseEntity
                .created(uriBuilder.path("/api/departments/{id}").build(created.getId()))
                .body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentDto> update(@PathVariable Long id,
                                                @Valid @RequestBody DepartmentDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.get(id));
    }

    @GetMapping
    public ResponseEntity<Page<DepartmentDto>> list(@RequestParam(required = false) String name,
                                                    @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return ResponseEntity.ok(service.list(name, pageable));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
