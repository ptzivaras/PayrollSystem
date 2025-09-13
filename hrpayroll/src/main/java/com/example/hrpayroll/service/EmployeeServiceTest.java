package com.example.hrpayroll.service;

import com.example.hrpayroll.dto.EmployeeDto;
import com.example.hrpayroll.entity.Department;
import com.example.hrpayroll.entity.Employee;
import com.example.hrpayroll.exception.BusinessException;
import com.example.hrpayroll.exception.ResourceNotFoundException;
import com.example.hrpayroll.mapper.EmployeeMapper;
import com.example.hrpayroll.repository.DepartmentRepository;
import com.example.hrpayroll.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeServiceTest {

    private EmployeeRepository employeeRepository;
    private DepartmentRepository departmentRepository;
    private EmployeeMapper mapper;
    private EmployeeService service;

    @BeforeEach
    void setUp() {
        employeeRepository = mock(EmployeeRepository.class);
        departmentRepository = mock(DepartmentRepository.class);
        mapper = new EmployeeMapper() {
            // lightweight mapper stub for unit test
            @Override
            public EmployeeDto toDto(Employee employee) {
                EmployeeDto dto = new EmployeeDto();
                dto.setId(employee.getId());
                dto.setFirstName(employee.getFirstName());
                dto.setLastName(employee.getLastName());
                dto.setEmail(employee.getEmail());
                dto.setDepartmentId(employee.getDepartment() != null ? employee.getDepartment().getId() : null);
                dto.setBaseSalary(employee.getBaseSalary());
                dto.setHireDate(employee.getHireDate());
                dto.setActive(employee.isActive());
                return dto;
            }
            @Override
            public Employee toEntity(EmployeeDto dto) {
                Employee e = new Employee();
                e.setId(dto.getId());
                e.setFirstName(dto.getFirstName());
                e.setLastName(dto.getLastName());
                e.setEmail(dto.getEmail());
                e.setBaseSalary(dto.getBaseSalary());
                e.setHireDate(dto.getHireDate());
                e.setActive(dto.isActive());
                if (dto.getDepartmentId() != null) {
                    Department d = new Department();
                    d.setId(dto.getDepartmentId());
                    e.setDepartment(d);
                }
                return e;
            }
        };
        service = new EmployeeService(employeeRepository, departmentRepository, mapper);
    }

    @Test
    void create_fails_whenEmailExists() {
        EmployeeDto dto = new EmployeeDto();
        dto.setFirstName("Ada");
        dto.setLastName("Lovelace");
        dto.setEmail("ada@example.com");
        dto.setBaseSalary(new BigDecimal("1200.00"));
        dto.setHireDate(LocalDate.now());

        when(employeeRepository.existsByEmail("ada@example.com")).thenReturn(true);

        assertThrows(BusinessException.class, () -> service.create(dto));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void update_throws_whenEmployeeNotFound() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());
        EmployeeDto dto = new EmployeeDto();
        dto.setEmail("new@example.com");
        assertThrows(ResourceNotFoundException.class, () -> service.update(99L, dto));
    }

    @Test
    void delete_throws_whenEmployeeNotFound() {
        when(employeeRepository.existsById(77L)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> service.delete(77L));
    }

    @Test
    void create_success_savesEntity() {
        EmployeeDto dto = new EmployeeDto();
        dto.setFirstName("Grace");
        dto.setLastName("Hopper");
        dto.setEmail("grace@example.com");
        dto.setBaseSalary(new BigDecimal("1500.00"));
        dto.setHireDate(LocalDate.now());
        dto.setDepartmentId(1L);

        when(employeeRepository.existsByEmail("grace@example.com")).thenReturn(false);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(new Department()));
        when(employeeRepository.save(ArgumentMatchers.any(Employee.class)))
                .thenAnswer(inv -> {
                    Employee e = inv.getArgument(0);
                    e.setId(1L);
                    return e;
                });

        EmployeeDto created = service.create(dto);
        assertNotNull(created.getId());
        assertEquals("grace@example.com", created.getEmail());
    }
}
