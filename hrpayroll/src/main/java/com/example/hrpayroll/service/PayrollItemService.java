package com.example.hrpayroll.service;

import com.example.hrpayroll.dto.PayrollItemDto;
import com.example.hrpayroll.entity.PayrollItem;
import com.example.hrpayroll.exception.ResourceNotFoundException;
import com.example.hrpayroll.mapper.PayrollItemMapper;
import com.example.hrpayroll.repository.PayrollItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class PayrollItemService {

    private final PayrollItemRepository repository;
    private final PayrollItemMapper mapper;

    public PayrollItemService(PayrollItemRepository repository, PayrollItemMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public Page<PayrollItemDto> listByRun(Long runId, Pageable pageable) {
        return repository.findAllByPayrollRun_Id(runId, pageable)
                .map(mapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<PayrollItemDto> listByEmployee(Long employeeId, LocalDate period, Pageable pageable) {
        return repository.findByEmployeeAndPeriod(employeeId, period, pageable)
                .map(mapper::toDto);
    }

    @Transactional(readOnly = true)
    public PayrollItemDto get(Long id) {
        PayrollItem item = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payroll item not found: " + id));
        return mapper.toDto(item);
    }
}
