package com.act.intern.employeedirectory.enterprise.infrastructure.persistence.employee.repository;

import com.act.intern.employeedirectory.enterprise.infrastructure.persistence.employee.entity.EmployeeJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface EmployeeJpaRepository extends JpaRepository<EmployeeJpaEntity, Long> {
    boolean existsByEmailAndDeletedFalse(String email);
    boolean existsByEmailAndIdNotAndDeletedFalse(String email, Long id);
    Optional<EmployeeJpaEntity> findByEmailAndDeletedFalse(String email);
    Optional<EmployeeJpaEntity> findByIdAndDeletedFalse(Long id);
    Page<EmployeeJpaEntity> findAllByDeletedFalse(Pageable pageable);
    Page<EmployeeJpaEntity> findByDepartmentIdAndDeletedFalse(Long departmentId, Pageable pageable);
    Page<EmployeeJpaEntity> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseAndDeletedFalse(
        String firstName, String lastName, Pageable pageable);
    Page<EmployeeJpaEntity> findBySalaryBetweenAndDeletedFalse(BigDecimal min, BigDecimal max, Pageable pageable);
}
