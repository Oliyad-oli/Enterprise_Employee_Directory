package com.act.intern.employeedirectory.enterprise.domain.employee;

import com.act.intern.employeedirectory.enterprise.domain.employee.aggregate.EmployeeAggregate;
import com.act.intern.employeedirectory.enterprise.domain.employee.event.EmployeeCreatedEvent;
import com.act.intern.employeedirectory.enterprise.domain.employee.exception.InvalidEmployeeStateException;
import com.act.intern.employeedirectory.enterprise.domain.employee.valueobject.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class EmployeeAggregateTest {

    @Test
    void should_create_employee_with_active_status() {
        var emp = EmployeeAggregate.create(
            EmployeeName.of("John", "Doe"),
            EmployeeEmail.of("john.doe@example.com"),
            Salary.of(BigDecimal.valueOf(50000)),
            LocalDate.of(2024, 1, 15),
            1L, "system"
        );

        assertThat(emp.getStatus()).isEqualTo(EmployeeStatus.ACTIVE);
        assertThat(emp.isDeleted()).isFalse();
        assertThat(emp.getDomainEvents()).hasSize(1);
        assertThat(emp.getDomainEvents().get(0)).isInstanceOf(EmployeeCreatedEvent.class);
    }

    @Test
    void should_soft_delete_employee() {
        var emp = EmployeeAggregate.create(
            EmployeeName.of("Jane", "Doe"),
            EmployeeEmail.of("jane.doe@example.com"),
            Salary.of(BigDecimal.valueOf(60000)),
            LocalDate.now(), 1L, "system"
        );
        emp.clearDomainEvents();
        emp.softDelete("admin");

        assertThat(emp.isDeleted()).isTrue();
        assertThat(emp.getStatus()).isEqualTo(EmployeeStatus.TERMINATED);
        assertThat(emp.getDomainEvents()).hasSize(1);
    }

    @Test
    void should_not_update_deleted_employee() {
        var emp = EmployeeAggregate.create(
            EmployeeName.of("Bob", "Smith"),
            EmployeeEmail.of("bob@example.com"),
            Salary.of(BigDecimal.valueOf(40000)),
            LocalDate.now(), 1L, "system"
        );
        emp.softDelete("admin");

        assertThatThrownBy(() -> emp.update(
            EmployeeName.of("Bob", "Smith"),
            EmployeeEmail.of("bob2@example.com"),
            Salary.of(BigDecimal.valueOf(40000)),
            LocalDate.now(), 1L, "admin"
        )).isInstanceOf(InvalidEmployeeStateException.class);
    }

    @Test
    void should_validate_email_format() {
        assertThatThrownBy(() -> EmployeeEmail.of("invalid-email"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid email format");
    }

    @Test
    void should_validate_salary_is_positive() {
        assertThatThrownBy(() -> Salary.of(BigDecimal.ZERO))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("positive");
    }

    @Test
    void should_validate_name_not_blank() {
        assertThatThrownBy(() -> EmployeeName.of("", "Doe"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void employee_name_should_return_full_name() {
        var name = EmployeeName.of("John", "Doe");
        assertThat(name.fullName()).isEqualTo("John Doe");
    }

    @Test
    void should_deactivate_employee() {
        var emp = EmployeeAggregate.create(
            EmployeeName.of("Alice", "Smith"),
            EmployeeEmail.of("alice@example.com"),
            Salary.of(BigDecimal.valueOf(70000)),
            LocalDate.now(), 1L, "system"
        );
        emp.deactivate();
        assertThat(emp.getStatus()).isEqualTo(EmployeeStatus.INACTIVE);
    }
}
