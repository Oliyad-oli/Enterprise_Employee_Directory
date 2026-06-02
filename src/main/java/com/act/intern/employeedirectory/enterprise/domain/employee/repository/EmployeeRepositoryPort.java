package com.act.intern.employeedirectory.enterprise.domain.employee.repository;

import com.act.intern.employeedirectory.enterprise.domain.employee.aggregate.EmployeeAggregate;
import com.act.intern.employeedirectory.enterprise.domain.employee.valueobject.EmployeeEmail;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepositoryPort {
    EmployeeAggregate save(EmployeeAggregate employee);
    Optional<EmployeeAggregate> findById(Long id);
    Optional<EmployeeAggregate> findByEmail(EmployeeEmail email);
    boolean existsByEmail(EmployeeEmail email);
    boolean existsByEmailAndIdNot(EmployeeEmail email, Long id);
    List<EmployeeAggregate> findAll(int page, int size, String sortBy);
    List<EmployeeAggregate> findByDepartmentId(Long departmentId, int page, int size, String sortBy);
    List<EmployeeAggregate> searchByName(String keyword, int page, int size, String sortBy);
    List<EmployeeAggregate> findBySalaryRange(BigDecimal min, BigDecimal max, int page, int size, String sortBy);
    void delete(EmployeeAggregate employee);
    long count();
}
