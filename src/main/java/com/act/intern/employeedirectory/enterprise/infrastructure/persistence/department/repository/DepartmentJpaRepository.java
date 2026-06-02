package com.act.intern.employeedirectory.enterprise.infrastructure.persistence.department.repository;

import com.act.intern.employeedirectory.enterprise.infrastructure.persistence.department.entity.DepartmentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentJpaRepository extends JpaRepository<DepartmentJpaEntity, Long> {
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
    Optional<DepartmentJpaEntity> findByIdAndDeletedFalse(Long id);
    List<DepartmentJpaEntity> findAllByDeletedFalse();
}
