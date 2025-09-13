package com.example.hrpayroll.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "payroll_run")
public class PayrollRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private LocalDate period; // first day of the month

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id",
            foreignKey = @ForeignKey(name = "fk_run_department"))
    private Department department;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private PayrollStatus status = PayrollStatus.PENDING;

    @Column(name = "created_at", nullable = false,
            columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @OneToMany(mappedBy = "payrollRun",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private Set<PayrollItem> payrollItems = new HashSet<>();

    // getters & setters
    // ...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getPeriod() { return period; }
    public void setPeriod(LocalDate period) { this.period = period; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public PayrollStatus getStatus() { return status; }
    public void setStatus(PayrollStatus status) { this.status = status; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public Set<PayrollItem> getPayrollItems() { return payrollItems; }
    public void setPayrollItems(Set<PayrollItem> payrollItems) { this.payrollItems = payrollItems; }
}
