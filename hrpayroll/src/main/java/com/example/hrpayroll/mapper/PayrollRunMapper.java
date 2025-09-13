package com.example.hrpayroll.mapper;

import com.example.hrpayroll.dto.PayrollRunDto;
import com.example.hrpayroll.entity.Department;
import com.example.hrpayroll.entity.PayrollRun;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PayrollRunMapper {

    @Mapping(source = "department.id", target = "departmentId")
    @Mapping(source = "status", target = "status")
    PayrollRunDto toDto(PayrollRun run);

    @Mapping(target = "department",
            expression = "java(departmentFromId(dto.getDepartmentId()))")
    @Mapping(target = "status",
            expression = "java(com.example.hrpayroll.entity.PayrollStatus.valueOf(dto.getStatus() == null ? \"PENDING\" : dto.getStatus()))")
    PayrollRun toEntity(PayrollRunDto dto);

    default Department departmentFromId(Long id) {
        if (id == null) return null;
        Department d = new Department();
        d.setId(id);
        return d;
    }
}
