package com.example.hrpayroll.dto;

import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public class PayrollItemDto {
    private Long id;
    private Long payrollRunId;
    private Long employeeId;

    @PositiveOrZero
    private BigDecimal grossAmount;

    @PositiveOrZero
    private BigDecimal taxAmount;

    @PositiveOrZero
    private BigDecimal netAmount;

    private String notes;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPayrollRunId() { return payrollRunId; }
    public void setPayrollRunId(Long payrollRunId) { this.payrollRunId = payrollRunId; }
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public BigDecimal getGrossAmount() { return grossAmount; }
    public void setGrossAmount(BigDecimal grossAmount) { this.grossAmount = grossAmount; }
    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }
    public BigDecimal getNetAmount() { return netAmount; }
    public void setNetAmount(BigDecimal netAmount) { this.netAmount = netAmount; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
