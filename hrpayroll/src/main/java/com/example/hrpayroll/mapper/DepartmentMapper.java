package com.example.hrpayroll.mapper;

import com.example.hrpayroll.dto.DepartmentDto;
import com.example.hrpayroll.entity.Department;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DepartmentMapper {
    DepartmentDto toDto(Department department);
    Department toEntity(DepartmentDto dto);
}
