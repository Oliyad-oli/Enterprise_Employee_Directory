package com.act.intern.employeedirectory.enterprise.application.department.command;

public record UpdateDepartmentCommand(Long departmentId, String name, String description, String requestedBy) {}
