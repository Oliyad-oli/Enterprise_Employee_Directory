package com.act.intern.employeedirectory.enterprise.domain.employee.event;

import com.act.intern.employeedirectory.enterprise.domain.shared.event.DomainEvent;

public class EmployeeCreatedEvent extends DomainEvent {
    private final Long employeeId;
    private final String email;
    private final Long departmentId;

    public EmployeeCreatedEvent(Long employeeId, String email, Long departmentId) {
        super("Employee", "EMPLOYEE_CREATED");
        this.employeeId = employeeId;
        this.email = email;
        this.departmentId = departmentId;
    }
    public Long getEmployeeId() { return employeeId; }
    public String getEmail() { return email; }
    public Long getDepartmentId() { return departmentId; }
}
