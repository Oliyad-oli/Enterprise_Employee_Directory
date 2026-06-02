package com.act.intern.employeedirectory.enterprise.infrastructure.persistence.employee.repository;

import com.act.intern.employeedirectory.enterprise.domain.employee.aggregate.EmployeeAggregate;
import com.act.intern.employeedirectory.enterprise.domain.employee.repository.EmployeeRepositoryPort;
import com.act.intern.employeedirectory.enterprise.domain.employee.valueobject.EmployeeEmail;
import com.act.intern.employeedirectory.enterprise.infrastructure.persistence.employee.mapper.EmployeePersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmployeeRepositoryAdapter implements EmployeeRepositoryPort {

    private final EmployeeJpaRepository jpaRepository;
    private final EmployeePersistenceMapper mapper;

    @Override
    public EmployeeAggregate save(EmployeeAggregate employee) {
        // Preserve domain events before saving (they are transient and not persisted)
        var preservedEvents = employee.getDomainEvents();
        log.debug("[REPOSITORY] Preserving {} domain events before persistence", preservedEvents.size());
        
        var entity = mapper.toEntity(employee);
        var saved = jpaRepository.save(entity);
        var reconstituted = mapper.toDomain(saved);
        
        // Restore domain events to the reconstituted aggregate
        // This ensures events are available for publishing after save
        reconstituted.restoreDomainEvents(preservedEvents);
        log.debug("[REPOSITORY] Restored {} domain events after persistence for employee ID: {}", 
            preservedEvents.size(), reconstituted.getId().value());
        
        return reconstituted;
    }

    @Override
    public Optional<EmployeeAggregate> findById(Long id) {
        return jpaRepository.findByIdAndDeletedFalse(id).map(mapper::toDomain);
    }

    @Override
    public Optional<EmployeeAggregate> findByEmail(EmployeeEmail email) {
        return jpaRepository.findByEmailAndDeletedFalse(email.value()).map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(EmployeeEmail email) {
        return jpaRepository.existsByEmailAndDeletedFalse(email.value());
    }

    @Override
    public boolean existsByEmailAndIdNot(EmployeeEmail email, Long id) {
        return jpaRepository.existsByEmailAndIdNotAndDeletedFalse(email.value(), id);
    }

    @Override
    public List<EmployeeAggregate> findAll(int page, int size, String sortBy) {
        var pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return jpaRepository.findAllByDeletedFalse(pageable).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<EmployeeAggregate> findByDepartmentId(Long departmentId, int page, int size, String sortBy) {
        var pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return jpaRepository.findByDepartmentIdAndDeletedFalse(departmentId, pageable).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<EmployeeAggregate> searchByName(String keyword, int page, int size, String sortBy) {
        var pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return jpaRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndDeletedFalse(
            keyword, keyword, pageable).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<EmployeeAggregate> findBySalaryRange(BigDecimal min, BigDecimal max, int page, int size, String sortBy) {
        var pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        return jpaRepository.findBySalaryBetweenAndDeletedFalse(min, max, pageable).stream().map(mapper::toDomain).toList();
    }

    @Override
    public void delete(EmployeeAggregate employee) {
        jpaRepository.findById(employee.getId().value()).ifPresent(e -> {
            e.setDeleted(true);
            jpaRepository.save(e);
        });
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }
}
