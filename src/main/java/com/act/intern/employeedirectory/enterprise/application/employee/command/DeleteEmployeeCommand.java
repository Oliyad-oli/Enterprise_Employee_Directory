package com.act.intern.employeedirectory.enterprise.application.employee.command;

public record DeleteEmployeeCommand(Long employeeId, String requestedBy) {}
