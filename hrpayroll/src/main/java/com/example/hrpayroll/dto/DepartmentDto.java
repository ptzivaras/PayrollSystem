package com.example.hrpayroll.dto;

import jakarta.validation.constraints.NotBlank;

public class DepartmentDto {
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String code;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
