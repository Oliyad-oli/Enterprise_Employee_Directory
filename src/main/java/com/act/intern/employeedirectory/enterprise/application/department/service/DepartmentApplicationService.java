package com.act.intern.employeedirectory.enterprise.application.department.service;

import com.act.intern.employeedirectory.enterprise.application.department.command.*;
import com.act.intern.employeedirectory.enterprise.application.department.handler.*;
import com.act.intern.employeedirectory.enterprise.application.department.query.*;
import com.act.intern.employeedirectory.enterprise.application.shared.port.AuditLogPort;
import com.act.intern.employeedirectory.enterprise.application.shared.port.DomainEventPublisherPort;
import com.act.intern.employeedirectory.enterprise.domain.department.aggregate.DepartmentAggregate;
import com.act.intern.employeedirectory.enterprise.domain.department.exception.DepartmentNotFoundException;
import com.act.intern.employeedirectory.enterprise.domain.department.exception.DuplicateDepartmentNameException;
import com.act.intern.employeedirectory.enterprise.domain.department.factory.DepartmentFactory;
import com.act.intern.employeedirectory.enterprise.domain.department.repository.DepartmentRepositoryPort;
import com.act.intern.employeedirectory.enterprise.domain.department.valueobject.DepartmentName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepartmentApplicationService implements
        CreateDepartmentUseCase, UpdateDepartmentUseCase, DeleteDepartmentUseCase,
        GetDepartmentUseCase, ListDepartmentsUseCase {

    private final DepartmentRepositoryPort departmentRepository;
    private final DomainEventPublisherPort eventPublisher;
    private final AuditLogPort auditLog;

    @Override
    @Transactional
    public Long handle(CreateDepartmentCommand command) {
        log.info("Creating department: {}", command.name());
        DepartmentName name = DepartmentName.of(command.name());
        if (departmentRepository.existsByName(name)) {
            throw new DuplicateDepartmentNameException(command.name());
        }
        DepartmentAggregate dept = DepartmentFactory.create(command.name(), command.description(), command.requestedBy());
        
        log.debug("Domain events registered before save: {}", dept.getDomainEvents().size());
        
        DepartmentAggregate saved = departmentRepository.save(dept);
        
        log.debug("Domain events after save/reconstitute: {}", saved.getDomainEvents().size());
        
        if (saved.getDomainEvents().isEmpty()) {
            log.warn("[WARNING] No domain events available for publishing! This will prevent Kafka notification.");
        }
        
        eventPublisher.publishAll(saved.getDomainEvents());
        log.info("[SUCCESS] Published {} domain events for department ID: {}", saved.getDomainEvents().size(), saved.getId().value());
        
        saved.clearDomainEvents();
        auditLog.log("CREATE", "Department", saved.getId().value(), command.requestedBy(), "Department created");
        return saved.getId().value();
    }

    @Override
    @Transactional
    public void handle(UpdateDepartmentCommand command) {
        log.info("Updating department id: {}", command.departmentId());
        DepartmentAggregate dept = departmentRepository.findById(command.departmentId())
            .orElseThrow(() -> new DepartmentNotFoundException(command.departmentId()));
        DepartmentName newName = DepartmentName.of(command.name());
        if (departmentRepository.existsByNameAndIdNot(newName, command.departmentId())) {
            throw new DuplicateDepartmentNameException(command.name());
        }
        dept.update(command.name(), command.description(), command.requestedBy());
        
        log.debug("Domain events registered before save (update): {}", dept.getDomainEvents().size());
        
        DepartmentAggregate updated = departmentRepository.save(dept);
        
        log.debug("Domain events after save/reconstitute (update): {}", updated.getDomainEvents().size());
        
        eventPublisher.publishAll(updated.getDomainEvents());
        log.info("[SUCCESS] Published {} domain events for updated department ID: {}", updated.getDomainEvents().size(), command.departmentId());
        
        updated.clearDomainEvents();
        auditLog.log("UPDATE", "Department", command.departmentId(), command.requestedBy(), "Department updated");
    }

    @Override
    @Transactional
    public void handle(DeleteDepartmentCommand command) {
        log.info("Deleting department id: {}", command.departmentId());
        DepartmentAggregate dept = departmentRepository.findById(command.departmentId())
            .orElseThrow(() -> new DepartmentNotFoundException(command.departmentId()));
        dept.softDelete(command.requestedBy());
        
        log.debug("Domain events registered before save (delete): {}", dept.getDomainEvents().size());
        
        DepartmentAggregate deleted = departmentRepository.save(dept);
        
        log.debug("Domain events after save/reconstitute (delete): {}", deleted.getDomainEvents().size());
        
        eventPublisher.publishAll(deleted.getDomainEvents());
        log.info("[SUCCESS] Published {} domain events for deleted department ID: {}", deleted.getDomainEvents().size(), command.departmentId());
        
        deleted.clearDomainEvents();
        auditLog.log("DELETE", "Department", command.departmentId(), command.requestedBy(), "Department soft-deleted");
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentAggregate handle(GetDepartmentQuery query) {
        return departmentRepository.findById(query.departmentId())
            .orElseThrow(() -> new DepartmentNotFoundException(query.departmentId()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentAggregate> handle(ListDepartmentsQuery query) {
        return departmentRepository.findAll();
    }
}
