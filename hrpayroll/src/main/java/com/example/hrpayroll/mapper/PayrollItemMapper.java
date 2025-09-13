package com.example.hrpayroll.mapper;

import com.example.hrpayroll.dto.PayrollItemDto;
import com.example.hrpayroll.entity.Employee;
import com.example.hrpayroll.entity.PayrollItem;
import com.example.hrpayroll.entity.PayrollRun;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PayrollItemMapper {

    @Mapping(source = "payrollRun.id", target = "payrollRunId")
    @Mapping(source = "employee.id", target = "employeeId")
    PayrollItemDto toDto(PayrollItem item);

    @Mapping(target = "payrollRun",
            expression = "java(runFromId(dto.getPayrollRunId()))")
    @Mapping(target = "employee",
            expression = "java(empFromId(dto.getEmployeeId()))")
    PayrollItem toEntity(PayrollItemDto dto);

    default PayrollRun runFromId(Long id) {
        if (id == null) return null;
        PayrollRun r = new PayrollRun();
        r.setId(id);
        return r;
    }

    default Employee empFromId(Long id) {
        if (id == null) return null;
        Employee e = new Employee();
        e.setId(id);
        return e;
    }
}
