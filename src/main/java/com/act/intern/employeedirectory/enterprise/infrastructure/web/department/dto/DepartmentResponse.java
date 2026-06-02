package com.act.intern.employeedirectory.enterprise.infrastructure.web.department.dto;

import java.time.LocalDateTime;

public record DepartmentResponse(
    Long id,
    String name,
    String description,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String createdBy
) {}
