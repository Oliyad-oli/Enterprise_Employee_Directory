package com.act.intern.employeedirectory.enterprise.infrastructure.persistence.department.repository;

import com.act.intern.employeedirectory.enterprise.domain.department.aggregate.DepartmentAggregate;
import com.act.intern.employeedirectory.enterprise.domain.department.repository.DepartmentRepositoryPort;
import com.act.intern.employeedirectory.enterprise.domain.department.valueobject.DepartmentName;
import com.act.intern.employeedirectory.enterprise.infrastructure.persistence.department.mapper.DepartmentPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DepartmentRepositoryAdapter implements DepartmentRepositoryPort {

    private final DepartmentJpaRepository jpaRepository;
    private final DepartmentPersistenceMapper mapper;

    @Override
    public DepartmentAggregate save(DepartmentAggregate department) {
        // Preserve domain events before saving (they are transient and not persisted)
        var preservedEvents = department.getDomainEvents();
        log.debug("[REPOSITORY] Preserving {} domain events before persistence", preservedEvents.size());
        
        var entity = mapper.toEntity(department);
        var saved = jpaRepository.save(entity);
        var reconstituted = mapper.toDomain(saved);
        
        // Restore domain events to the reconstituted aggregate
        // This ensures events are available for publishing after save
        reconstituted.restoreDomainEvents(preservedEvents);
        log.debug("[REPOSITORY] Restored {} domain events after persistence for department ID: {}", 
            preservedEvents.size(), reconstituted.getId().value());
        
        return reconstituted;
    }

    @Override
    public Optional<DepartmentAggregate> findById(Long id) {
        return jpaRepository.findByIdAndDeletedFalse(id).map(mapper::toDomain);
    }

    @Override
    public boolean existsByName(DepartmentName name) {
        return jpaRepository.existsByNameIgnoreCase(name.value());
    }

    @Override
    public boolean existsByNameAndIdNot(DepartmentName name, Long id) {
        return jpaRepository.existsByNameIgnoreCaseAndIdNot(name.value(), id);
    }

    @Override
    public List<DepartmentAggregate> findAll() {
        return jpaRepository.findAllByDeletedFalse().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void delete(DepartmentAggregate department) {
        jpaRepository.findById(department.getId().value()).ifPresent(e -> {
            e.setDeleted(true);
            jpaRepository.save(e);
        });
    }
}
