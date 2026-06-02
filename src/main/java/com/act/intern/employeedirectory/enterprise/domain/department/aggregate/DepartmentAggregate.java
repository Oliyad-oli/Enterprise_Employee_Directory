package com.act.intern.employeedirectory.enterprise.domain.department.aggregate;

import com.act.intern.employeedirectory.enterprise.domain.department.event.*;
import com.act.intern.employeedirectory.enterprise.domain.department.valueobject.*;
import com.act.intern.employeedirectory.enterprise.domain.shared.event.DomainEvent;
import com.act.intern.employeedirectory.enterprise.domain.shared.valueobject.AuditInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DepartmentAggregate {

    private DepartmentId id;
    private DepartmentName name;
    private String description;
    private AuditInfo auditInfo;
    private boolean deleted;
    private int version;

    private final List<DomainEvent> domainEvents = new ArrayList<>();

    private DepartmentAggregate() {}

    public static DepartmentAggregate create(String name, String description, String createdBy) {
        DepartmentAggregate dept = new DepartmentAggregate();
        dept.name = DepartmentName.of(name);
        dept.description = description;
        dept.auditInfo = AuditInfo.createNew(createdBy);
        dept.deleted = false;
        dept.version = 0;
        dept.registerEvent(new DepartmentCreatedEvent(null, name));
        return dept;
    }

    public static DepartmentAggregate reconstitute(Long id, String name, String description,
                                                    AuditInfo auditInfo, boolean deleted, int version) {
        DepartmentAggregate dept = new DepartmentAggregate();
        dept.id = id != null ? DepartmentId.of(id) : null;
        dept.name = DepartmentName.of(name);
        dept.description = description;
        dept.auditInfo = auditInfo;
        dept.deleted = deleted;
        dept.version = version;
        return dept;
    }

    public void update(String name, String description, String updatedBy) {
        if (deleted) throw new IllegalStateException("Cannot update a deleted department");
        this.name = DepartmentName.of(name);
        this.description = description;
        this.auditInfo = auditInfo.withUpdatedBy(updatedBy);
        registerEvent(new DepartmentUpdatedEvent(id != null ? id.value() : null));
    }

    public void softDelete(String deletedBy) {
        this.deleted = true;
        this.auditInfo = auditInfo.withUpdatedBy(deletedBy);
        registerEvent(new DepartmentDeletedEvent(id != null ? id.value() : null));
    }

    public void assignId(Long id) { this.id = DepartmentId.of(id); }

    private void registerEvent(DomainEvent event) { domainEvents.add(event); }

    /**
     * Restore domain events that were preserved before persistence.
     * Used by repository adapters to preserve transient domain events
     * across save/reconstitute cycle.
     */
    public void restoreDomainEvents(List<DomainEvent> events) {
        domainEvents.addAll(events);
    }

    public List<DomainEvent> getDomainEvents() { return Collections.unmodifiableList(domainEvents); }
    public void clearDomainEvents() { domainEvents.clear(); }

    public DepartmentId getId() { return id; }
    public DepartmentName getName() { return name; }
    public String getDescription() { return description; }
    public AuditInfo getAuditInfo() { return auditInfo; }
    public boolean isDeleted() { return deleted; }
    public int getVersion() { return version; }
}
