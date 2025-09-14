package com.example.hrpayroll.mapper;

import com.example.hrpayroll.dto.EmployeeDto;
import com.example.hrpayroll.entity.Department;
import com.example.hrpayroll.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface EmployeeMapper {

    @Mapping(target = "departmentId",
            expression = "java(employee.getDepartment() != null ? employee.getDepartment().getId() : null)")
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
