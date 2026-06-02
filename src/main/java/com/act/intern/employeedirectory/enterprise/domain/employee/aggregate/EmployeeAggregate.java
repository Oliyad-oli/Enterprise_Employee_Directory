package com.act.intern.employeedirectory.enterprise.domain.employee.aggregate;

import com.act.intern.employeedirectory.enterprise.domain.employee.event.*;
import com.act.intern.employeedirectory.enterprise.domain.employee.exception.InvalidEmployeeStateException;
import com.act.intern.employeedirectory.enterprise.domain.employee.valueobject.*;
import com.act.intern.employeedirectory.enterprise.domain.shared.event.DomainEvent;
import com.act.intern.employeedirectory.enterprise.domain.shared.valueobject.AuditInfo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class EmployeeAggregate {

    private EmployeeId id;
    private EmployeeName name;
    private EmployeeEmail email;
    private Salary salary;
    private LocalDate hireDate;
    private EmployeeStatus status;
    private Long departmentId;
    private AuditInfo auditInfo;
    private boolean deleted;
    private int version;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    private EmployeeAggregate() {}

    // Factory method for new employee
    public static EmployeeAggregate create(
            EmployeeName name,
            EmployeeEmail email,
            Salary salary,
            LocalDate hireDate,
            Long departmentId,
            String createdBy) {

        Objects.requireNonNull(departmentId, "Department ID is required");
        Objects.requireNonNull(hireDate, "Hire date is required");

        EmployeeAggregate employee = new EmployeeAggregate();
        employee.name = name;
        employee.email = email;
        employee.salary = salary;
        employee.hireDate = hireDate;
        employee.departmentId = departmentId;
        employee.status = EmployeeStatus.ACTIVE;
        employee.auditInfo = AuditInfo.createNew(createdBy);
        employee.deleted = false;
        employee.version = 0;

        employee.registerEvent(new EmployeeCreatedEvent(null, email.value(), departmentId));
        return employee;
    }

    // Reconstitute from persistence
    public static EmployeeAggregate reconstitute(
            Long id, String firstName, String lastName, String email,
            java.math.BigDecimal salary, LocalDate hireDate,
            String status, Long departmentId,
            AuditInfo auditInfo, boolean deleted, int version) {
        EmployeeAggregate emp = new EmployeeAggregate();
        emp.id = id != null ? EmployeeId.of(id) : null;
        emp.name = EmployeeName.of(firstName, lastName);
        emp.email = EmployeeEmail.of(email);
        emp.salary = Salary.of(salary);
        emp.hireDate = hireDate;
        emp.status = EmployeeStatus.valueOf(status);
        emp.departmentId = departmentId;
        emp.auditInfo = auditInfo;
        emp.deleted = deleted;
        emp.version = version;
        return emp;
    }

    public void update(EmployeeName name, EmployeeEmail email, Salary salary,
                       LocalDate hireDate, Long departmentId, String updatedBy) {
        if (deleted) throw new InvalidEmployeeStateException("Cannot update a deleted employee");
        Objects.requireNonNull(departmentId, "Department ID is required");
        this.name = name;
        this.email = email;
        this.salary = salary;
        this.hireDate = hireDate;
        this.departmentId = departmentId;
        this.auditInfo = auditInfo.withUpdatedBy(updatedBy);
        registerEvent(new EmployeeUpdatedEvent(id != null ? id.value() : null));
    }

    public void activate() {
        if (deleted) throw new InvalidEmployeeStateException("Cannot activate a deleted employee");
        this.status = EmployeeStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = EmployeeStatus.INACTIVE;
    }

    public void softDelete(String deletedBy) {
        this.deleted = true;
        this.status = EmployeeStatus.TERMINATED;
        this.auditInfo = auditInfo.withUpdatedBy(deletedBy);
        registerEvent(new EmployeeDeletedEvent(id != null ? id.value() : null));
    }

    public void assignId(Long id) {
        if (this.id != null) throw new InvalidEmployeeStateException("Employee already has an ID");
        this.id = EmployeeId.of(id);
    }

    private void registerEvent(DomainEvent event) {
        domainEvents.add(event);
    }

    /**
     * Restore domain events that were preserved before persistence.
     * Used by repository adapters to preserve transient domain events
     * across save/reconstitute cycle.
     */
    public void restoreDomainEvents(java.util.List<DomainEvent> events) {
        domainEvents.addAll(events);
    }

    public List<DomainEvent> getDomainEvents() { return Collections.unmodifiableList(domainEvents); }
    public void clearDomainEvents() { domainEvents.clear(); }

    // Getters
    public EmployeeId getId() { return id; }
    public EmployeeName getName() { return name; }
    public EmployeeEmail getEmail() { return email; }
    public Salary getSalary() { return salary; }
    public LocalDate getHireDate() { return hireDate; }
    public EmployeeStatus getStatus() { return status; }
    public Long getDepartmentId() { return departmentId; }
    public AuditInfo getAuditInfo() { return auditInfo; }
    public boolean isDeleted() { return deleted; }
    public int getVersion() { return version; }
}
