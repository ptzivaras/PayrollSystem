package com.example.hrpayroll.service;

import com.example.hrpayroll.dto.DepartmentDto;
import com.example.hrpayroll.entity.Department;
import com.example.hrpayroll.exception.BusinessException;
import com.example.hrpayroll.exception.ResourceNotFoundException;
import com.example.hrpayroll.mapper.DepartmentMapper;
import com.example.hrpayroll.repository.DepartmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DepartmentService {

    private final DepartmentRepository repository;
    private final DepartmentMapper mapper;

    public DepartmentService(DepartmentRepository repository, DepartmentMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public DepartmentDto create(DepartmentDto dto) {
        if (repository.existsByCode(dto.getCode())) {
            throw new BusinessException("Department code already exists: " + dto.getCode());
        }
        Department entity = mapper.toEntity(dto);
        Department saved = repository.save(entity);
        return mapper.toDto(saved);
    }

    @Transactional
    public DepartmentDto update(Long id, DepartmentDto dto) {
        Department dep = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + id));

        if (!dep.getCode().equals(dto.getCode()) && repository.existsByCode(dto.getCode())) {
            throw new BusinessException("Department code already exists: " + dto.getCode());
        }

        dep.setName(dto.getName());
        dep.setCode(dto.getCode());
        Department saved = repository.save(dep);
        return mapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public DepartmentDto get(Long id) {
        Department dep = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + id));
        return mapper.toDto(dep);
    }

    @Transactional(readOnly = true)
    public Page<DepartmentDto> list(String name, Pageable pageable) {
        Page<Department> page = (name == null || name.isBlank())
                ? repository.findAll(pageable)
                : repository.findByNameContainingIgnoreCase(name, pageable);
        return page.map(mapper::toDto);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Department not found: " + id);
        }
        repository.deleteById(id);
    }
}
