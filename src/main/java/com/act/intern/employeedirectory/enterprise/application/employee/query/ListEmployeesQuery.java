package com.act.intern.employeedirectory.enterprise.application.employee.query;

public record ListEmployeesQuery(Long departmentId, int page, int size, String sortBy) {}
