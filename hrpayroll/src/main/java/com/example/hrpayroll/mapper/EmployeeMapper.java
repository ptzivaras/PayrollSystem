package com.example.hrpayroll.mapper;

import com.example.hrpayroll.dto.EmployeeDto;
import com.example.hrpayroll.entity.Department;
import com.example.hrpayroll.entity.Employee;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EmployeeMapper {

    @Mapping(source = "department.id", target = "departmentId")
    EmployeeDto toDto(Employee employee);

    @Mapping(target = "department",
            expression = "java(departmentFromId(dto.getDepartmentId()))")
    Employee toEntity(EmployeeDto dto);

    default Department departmentFromId(Long id) {
        if (id == null) return null;
        Department d = new Department();
        d.setId(id);
        return d;
    }
}
