package com.example.hrpayroll.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class PayrollRunDto {
    private Long id;

    @NotNull
    private LocalDate period;

    private Long departmentId;

    private String status;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getPeriod() { return period; }
    public void setPeriod(LocalDate period) { this.period = period; }
    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
