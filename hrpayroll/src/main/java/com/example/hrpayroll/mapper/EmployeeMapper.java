package com.example.hrpayroll.mapper;

import com.example.hrpayroll.dto.EmployeeDto;
import com.example.hrpayroll.entity.Department;
import com.example.hrpayroll.entity.Employee;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface EmployeeMapper {

    @Mappings({
            @Mapping(target = "departmentId",
                    expression = "java(employee.getDepartment() != null ? employee.getDepartment().getId() : null)"),
            @Mapping(target = "departmentName",
                    expression = "java(employee.getDepartment() != null ? employee.getDepartment().getName() : null)")
    })
    EmployeeDto toDto(Employee employee);

    @Mappings({
            @Mapping(target = "department",
                    expression = "java(departmentFromId(dto.getDepartmentId()))"),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "payrollItems", ignore = true)
    })
    Employee toEntity(EmployeeDto dto);

    default Department departmentFromId(Long id) {
        if (id == null) return null;
        Department d = new Department();
        d.setId(id);
        return d;
    }
}
