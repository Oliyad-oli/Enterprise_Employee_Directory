package com.act.intern.employeedirectory.enterprise.application.employee;

import com.act.intern.employeedirectory.enterprise.application.employee.command.*;
import com.act.intern.employeedirectory.enterprise.application.employee.query.*;
import com.act.intern.employeedirectory.enterprise.application.employee.service.EmployeeApplicationService;
import com.act.intern.employeedirectory.enterprise.application.shared.port.AuditLogPort;
import com.act.intern.employeedirectory.enterprise.application.shared.port.DomainEventPublisherPort;
import com.act.intern.employeedirectory.enterprise.domain.department.aggregate.DepartmentAggregate;
import com.act.intern.employeedirectory.enterprise.domain.department.repository.DepartmentRepositoryPort;
import com.act.intern.employeedirectory.enterprise.domain.employee.aggregate.EmployeeAggregate;
import com.act.intern.employeedirectory.enterprise.domain.employee.exception.DuplicateEmployeeEmailException;
import com.act.intern.employeedirectory.enterprise.domain.employee.exception.EmployeeNotFoundException;
import com.act.intern.employeedirectory.enterprise.domain.employee.repository.EmployeeRepositoryPort;
import com.act.intern.employeedirectory.enterprise.domain.shared.valueobject.AuditInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeApplicationServiceTest {

    @Mock private EmployeeRepositoryPort employeeRepository;
    @Mock private DepartmentRepositoryPort departmentRepository;
    @Mock private DomainEventPublisherPort eventPublisher;
    @Mock private AuditLogPort auditLog;

    @InjectMocks private EmployeeApplicationService service;

    private DepartmentAggregate mockDept;

    @BeforeEach
    void setUp() {
        mockDept = DepartmentAggregate.reconstitute(1L, "Engineering", "Desc",
            AuditInfo.createNew("system"), false, 0);
    }

    @Test
    void should_create_employee_successfully() {
        var cmd = new CreateEmployeeCommand("John", "Doe", "john@example.com",
            BigDecimal.valueOf(50000), LocalDate.now(), 1L, "system");

        when(employeeRepository.existsByEmail(any())).thenReturn(false);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(mockDept));

        var saved = EmployeeAggregate.reconstitute(1L, "John", "Doe", "john@example.com",
            BigDecimal.valueOf(50000), LocalDate.now(), "ACTIVE", 1L,
            AuditInfo.createNew("system"), false, 0);
        when(employeeRepository.save(any())).thenReturn(saved);

        Long id = service.handle(cmd);

        assertThat(id).isEqualTo(1L);
        verify(eventPublisher).publishAll(any());
        verify(auditLog).log(eq("CREATE"), eq("Employee"), eq(1L), eq("system"), any());
    }

    @Test
    void should_throw_when_email_exists() {
        var cmd = new CreateEmployeeCommand("John", "Doe", "existing@example.com",
            BigDecimal.valueOf(50000), LocalDate.now(), 1L, "system");

        when(employeeRepository.existsByEmail(any())).thenReturn(true);

        assertThatThrownBy(() -> service.handle(cmd))
            .isInstanceOf(DuplicateEmployeeEmailException.class);
    }

    @Test
    void should_throw_when_employee_not_found() {
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.handle(new GetEmployeeQuery(999L)))
            .isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void should_list_employees() {
        var emp = EmployeeAggregate.reconstitute(1L, "John", "Doe", "john@example.com",
            BigDecimal.valueOf(50000), LocalDate.now(), "ACTIVE", 1L,
            AuditInfo.createNew("system"), false, 0);
        when(employeeRepository.findAll(0, 10, "id")).thenReturn(List.of(emp));

        var result = service.handle(new ListEmployeesQuery(null, 0, 10, "id"));

        assertThat(result).hasSize(1);
    }

    @Test
    void should_delete_employee_with_soft_delete() {
        var emp = EmployeeAggregate.reconstitute(1L, "John", "Doe", "john@example.com",
            BigDecimal.valueOf(50000), LocalDate.now(), "ACTIVE", 1L,
            AuditInfo.createNew("system"), false, 0);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(emp));

        var deleted = EmployeeAggregate.reconstitute(1L, "John", "Doe", "john@example.com",
            BigDecimal.valueOf(50000), LocalDate.now(), "TERMINATED", 1L,
            AuditInfo.createNew("system"), true, 1);
        when(employeeRepository.save(any())).thenReturn(deleted);

        service.handle(new DeleteEmployeeCommand(1L, "admin"));

        verify(employeeRepository).save(any());
        verify(eventPublisher).publishAll(any());
    }
}
