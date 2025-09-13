package com.example.hrpayroll.service;

import com.example.hrpayroll.dto.EmployeeDto;
import com.example.hrpayroll.entity.Department;
import com.example.hrpayroll.entity.Employee;
import com.example.hrpayroll.exception.BusinessException;
import com.example.hrpayroll.exception.ResourceNotFoundException;
import com.example.hrpayroll.mapper.EmployeeMapper;
import com.example.hrpayroll.repository.DepartmentRepository;
import com.example.hrpayroll.repository.EmployeeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService {

    private final EmployeeRepository repository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper mapper;

    public EmployeeService(EmployeeRepository repository,
                           DepartmentRepository departmentRepository,
                           EmployeeMapper mapper) {
        this.repository = repository;
        this.departmentRepository = departmentRepository;
        this.mapper = mapper;
    }

    @Transactional
    public EmployeeDto create(EmployeeDto dto) {
        if (repository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Email already exists: " + dto.getEmail());
        }

        Employee entity = mapper.toEntity(dto);

        if (dto.getDepartmentId() != null) {
            Department dep = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + dto.getDepartmentId()));
            entity.setDepartment(dep);
        }

        Employee saved = repository.save(entity);
        return mapper.toDto(saved);
    }

    @Transactional
    public EmployeeDto update(Long id, EmployeeDto dto) {
        Employee emp = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id));

        if (!emp.getEmail().equals(dto.getEmail()) && repository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Email already exists: " + dto.getEmail());
        }

        emp.setFirstName(dto.getFirstName());
        emp.setLastName(dto.getLastName());
        emp.setEmail(dto.getEmail());
        emp.setBaseSalary(dto.getBaseSalary());
        emp.setHireDate(dto.getHireDate());
        emp.setActive(dto.isActive());

        if (dto.getDepartmentId() != null) {
            Department dep = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + dto.getDepartmentId()));
            emp.setDepartment(dep);
        } else {
            emp.setDepartment(null);
        }

        Employee saved = repository.save(emp);
        return mapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public EmployeeDto get(Long id) {
        Employee emp = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + id));
        return mapper.toDto(emp);
    }

    @Transactional(readOnly = true)
    public Page<EmployeeDto> search(String q, Long departmentId, Pageable pageable) {
        if (departmentId != null) {
            return repository.findAllByDepartment_Id(departmentId, pageable)
                    .map(mapper::toDto);
        }
        return repository.search(q, pageable).map(mapper::toDto);
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Employee not found: " + id);
        }
        repository.deleteById(id);
    }
}
