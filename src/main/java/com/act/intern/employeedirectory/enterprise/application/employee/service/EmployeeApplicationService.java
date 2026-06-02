package com.act.intern.employeedirectory.enterprise.application.employee.service;

import com.act.intern.employeedirectory.enterprise.application.employee.command.*;
import com.act.intern.employeedirectory.enterprise.application.employee.handler.*;
import com.act.intern.employeedirectory.enterprise.application.employee.query.*;
import com.act.intern.employeedirectory.enterprise.application.shared.port.AuditLogPort;
import com.act.intern.employeedirectory.enterprise.application.shared.port.DomainEventPublisherPort;
import com.act.intern.employeedirectory.enterprise.domain.department.exception.DepartmentNotFoundException;
import com.act.intern.employeedirectory.enterprise.domain.department.repository.DepartmentRepositoryPort;
import com.act.intern.employeedirectory.enterprise.domain.employee.aggregate.EmployeeAggregate;
import com.act.intern.employeedirectory.enterprise.domain.employee.exception.DuplicateEmployeeEmailException;
import com.act.intern.employeedirectory.enterprise.domain.employee.exception.EmployeeNotFoundException;
import com.act.intern.employeedirectory.enterprise.domain.employee.factory.EmployeeFactory;
import com.act.intern.employeedirectory.enterprise.domain.employee.repository.EmployeeRepositoryPort;
import com.act.intern.employeedirectory.enterprise.domain.employee.valueobject.EmployeeEmail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeApplicationService implements
        CreateEmployeeUseCase, UpdateEmployeeUseCase, DeleteEmployeeUseCase,
        GetEmployeeUseCase, ListEmployeesUseCase {

    private final EmployeeRepositoryPort employeeRepository;
    private final DepartmentRepositoryPort departmentRepository;
    private final DomainEventPublisherPort eventPublisher;
    private final AuditLogPort auditLog;

    @Override
    @Transactional
    public Long handle(CreateEmployeeCommand command) {
        log.info("Creating employee with email: {}", command.email());

        EmployeeEmail email = EmployeeEmail.of(command.email());
        if (employeeRepository.existsByEmail(email)) {
            throw new DuplicateEmployeeEmailException(command.email());
        }

        // Verify department exists
        departmentRepository.findById(command.departmentId())
            .orElseThrow(() -> new DepartmentNotFoundException(command.departmentId()));

        EmployeeAggregate employee = EmployeeFactory.create(
            command.firstName(), command.lastName(), command.email(),
            command.salary(), command.hireDate(), command.departmentId(), command.requestedBy()
        );

        log.debug("Domain events registered before save: {}", employee.getDomainEvents().size());
        
        EmployeeAggregate saved = employeeRepository.save(employee);
        
        log.debug("Domain events after save/reconstitute: {}", saved.getDomainEvents().size());
        
        if (saved.getDomainEvents().isEmpty()) {
            log.warn("[WARNING] No domain events available for publishing! This will prevent Kafka notification.");
        }
        
        eventPublisher.publishAll(saved.getDomainEvents());
        log.info("[SUCCESS] Published {} domain events for employee ID: {}", saved.getDomainEvents().size(), saved.getId().value());
        
        saved.clearDomainEvents();
        auditLog.log("CREATE", "Employee", saved.getId().value(), command.requestedBy(), "Employee created");
        return saved.getId().value();
    }

    @Override
    @Transactional
    public void handle(UpdateEmployeeCommand command) {
        log.info("Updating employee id: {}", command.employeeId());

        EmployeeAggregate employee = employeeRepository.findById(command.employeeId())
            .orElseThrow(() -> new EmployeeNotFoundException(command.employeeId()));

        EmployeeEmail newEmail = EmployeeEmail.of(command.email());
        if (employeeRepository.existsByEmailAndIdNot(newEmail, command.employeeId())) {
            throw new DuplicateEmployeeEmailException(command.email());
        }

        departmentRepository.findById(command.departmentId())
            .orElseThrow(() -> new DepartmentNotFoundException(command.departmentId()));

        employee.update(
            com.act.intern.employeedirectory.enterprise.domain.employee.valueobject.EmployeeName.of(command.firstName(), command.lastName()),
            newEmail,
            com.act.intern.employeedirectory.enterprise.domain.employee.valueobject.Salary.of(command.salary()),
            command.hireDate(), command.departmentId(), command.requestedBy()
        );

        log.debug("Domain events registered before save (update): {}", employee.getDomainEvents().size());
        
        EmployeeAggregate updated = employeeRepository.save(employee);
        
        log.debug("Domain events after save/reconstitute (update): {}", updated.getDomainEvents().size());
        
        eventPublisher.publishAll(updated.getDomainEvents());
        log.info("[SUCCESS] Published {} domain events for updated employee ID: {}", updated.getDomainEvents().size(), command.employeeId());
        
        updated.clearDomainEvents();
        auditLog.log("UPDATE", "Employee", command.employeeId(), command.requestedBy(), "Employee updated");
    }

    @Override
    @Transactional
    public void handle(DeleteEmployeeCommand command) {
        log.info("Deleting employee id: {}", command.employeeId());

        EmployeeAggregate employee = employeeRepository.findById(command.employeeId())
            .orElseThrow(() -> new EmployeeNotFoundException(command.employeeId()));

        employee.softDelete(command.requestedBy());
        
        log.debug("Domain events registered before save (delete): {}", employee.getDomainEvents().size());
        
        EmployeeAggregate deleted = employeeRepository.save(employee);
        
        log.debug("Domain events after save/reconstitute (delete): {}", deleted.getDomainEvents().size());
        
        eventPublisher.publishAll(deleted.getDomainEvents());
        log.info("[SUCCESS] Published {} domain events for deleted employee ID: {}", deleted.getDomainEvents().size(), command.employeeId());
        
        deleted.clearDomainEvents();
        auditLog.log("DELETE", "Employee", command.employeeId(), command.requestedBy(), "Employee soft-deleted");
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeAggregate handle(GetEmployeeQuery query) {
        return employeeRepository.findById(query.employeeId())
            .orElseThrow(() -> new EmployeeNotFoundException(query.employeeId()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeAggregate> handle(ListEmployeesQuery query) {
        return employeeRepository.findAll(query.page(), query.size(), query.sortBy());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeAggregate> handle(SearchEmployeesQuery query) {
        if (query.keyword() != null && !query.keyword().isBlank()) {
            return employeeRepository.searchByName(query.keyword(), query.page(), query.size(), query.sortBy());
        }
        if (query.minSalary() != null && query.maxSalary() != null) {
            return employeeRepository.findBySalaryRange(query.minSalary(), query.maxSalary(), query.page(), query.size(), query.sortBy());
        }
        return employeeRepository.findAll(query.page(), query.size(), query.sortBy());
    }
}
