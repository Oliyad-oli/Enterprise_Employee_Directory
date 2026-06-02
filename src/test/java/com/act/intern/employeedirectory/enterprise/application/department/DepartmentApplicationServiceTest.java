package com.act.intern.employeedirectory.enterprise.application.department;

import com.act.intern.employeedirectory.enterprise.application.department.command.*;
import com.act.intern.employeedirectory.enterprise.application.department.query.*;
import com.act.intern.employeedirectory.enterprise.application.department.service.DepartmentApplicationService;
import com.act.intern.employeedirectory.enterprise.application.shared.port.AuditLogPort;
import com.act.intern.employeedirectory.enterprise.application.shared.port.DomainEventPublisherPort;
import com.act.intern.employeedirectory.enterprise.domain.department.aggregate.DepartmentAggregate;
import com.act.intern.employeedirectory.enterprise.domain.department.exception.DepartmentNotFoundException;
import com.act.intern.employeedirectory.enterprise.domain.department.exception.DuplicateDepartmentNameException;
import com.act.intern.employeedirectory.enterprise.domain.department.repository.DepartmentRepositoryPort;
import com.act.intern.employeedirectory.enterprise.domain.shared.valueobject.AuditInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentApplicationServiceTest {

    @Mock private DepartmentRepositoryPort departmentRepository;
    @Mock private DomainEventPublisherPort eventPublisher;
    @Mock private AuditLogPort auditLog;

    @InjectMocks private DepartmentApplicationService service;

    @Test
    void should_create_department_successfully() {
        when(departmentRepository.existsByName(any())).thenReturn(false);
        var saved = DepartmentAggregate.reconstitute(1L, "Engineering", "Desc",
            AuditInfo.createNew("system"), false, 0);
        when(departmentRepository.save(any())).thenReturn(saved);

        Long id = service.handle(new CreateDepartmentCommand("Engineering", "Desc", "system"));

        assertThat(id).isEqualTo(1L);
        verify(eventPublisher).publishAll(any());
    }

    @Test
    void should_throw_on_duplicate_name() {
        when(departmentRepository.existsByName(any())).thenReturn(true);

        assertThatThrownBy(() -> service.handle(new CreateDepartmentCommand("Engineering", null, "system")))
            .isInstanceOf(DuplicateDepartmentNameException.class);
    }

    @Test
    void should_list_all_departments() {
        var dept = DepartmentAggregate.reconstitute(1L, "HR", "Human Resources",
            AuditInfo.createNew("system"), false, 0);
        when(departmentRepository.findAll()).thenReturn(List.of(dept));

        var result = service.handle(new ListDepartmentsQuery());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName().value()).isEqualTo("HR");
    }

    @Test
    void should_throw_when_department_not_found() {
        when(departmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.handle(new GetDepartmentQuery(99L)))
            .isInstanceOf(DepartmentNotFoundException.class);
    }

    @Test
    void should_delete_department() {
        var dept = DepartmentAggregate.reconstitute(1L, "Finance", "Finance dept",
            AuditInfo.createNew("system"), false, 0);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(dept));
        var deleted = DepartmentAggregate.reconstitute(1L, "Finance", "Finance dept",
            AuditInfo.createNew("system"), true, 1);
        when(departmentRepository.save(any())).thenReturn(deleted);

        service.handle(new DeleteDepartmentCommand(1L, "admin"));

        verify(departmentRepository).save(any());
    }
}
