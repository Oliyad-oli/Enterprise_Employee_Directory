package com.act.intern.employeedirectory.enterprise.infrastructure.web.department.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateDepartmentRequest(
    @NotBlank(message = "Department name is required") String name,
    String description
) {}
