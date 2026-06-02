package com.act.intern.employeedirectory.enterprise.domain.department.repository;

import com.act.intern.employeedirectory.enterprise.domain.department.aggregate.DepartmentAggregate;
import com.act.intern.employeedirectory.enterprise.domain.department.valueobject.DepartmentName;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepositoryPort {
    DepartmentAggregate save(DepartmentAggregate department);
    Optional<DepartmentAggregate> findById(Long id);
    boolean existsByName(DepartmentName name);
    boolean existsByNameAndIdNot(DepartmentName name, Long id);
    List<DepartmentAggregate> findAll();
    void delete(DepartmentAggregate department);
}
