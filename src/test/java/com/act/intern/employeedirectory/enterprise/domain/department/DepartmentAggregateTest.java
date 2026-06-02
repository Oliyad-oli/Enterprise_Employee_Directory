package com.act.intern.employeedirectory.enterprise.domain.department;

import com.act.intern.employeedirectory.enterprise.domain.department.aggregate.DepartmentAggregate;
import com.act.intern.employeedirectory.enterprise.domain.department.event.DepartmentCreatedEvent;
import com.act.intern.employeedirectory.enterprise.domain.department.event.DepartmentDeletedEvent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class DepartmentAggregateTest {

    @Test
    void should_create_department_with_event() {
        var dept = DepartmentAggregate.create("Engineering", "Engineering team", "system");

        assertThat(dept.getName().value()).isEqualTo("Engineering");
        assertThat(dept.isDeleted()).isFalse();
        assertThat(dept.getDomainEvents()).hasSize(1);
        assertThat(dept.getDomainEvents().get(0)).isInstanceOf(DepartmentCreatedEvent.class);
    }

    @Test
    void should_soft_delete_department() {
        var dept = DepartmentAggregate.create("HR", "Human Resources", "system");
        dept.clearDomainEvents();
        dept.softDelete("admin");

        assertThat(dept.isDeleted()).isTrue();
        assertThat(dept.getDomainEvents()).hasSize(1);
        assertThat(dept.getDomainEvents().get(0)).isInstanceOf(DepartmentDeletedEvent.class);
    }

    @Test
    void should_update_department() {
        var dept = DepartmentAggregate.create("IT", "IT Department", "system");
        dept.update("Information Technology", "Updated description", "admin");

        assertThat(dept.getName().value()).isEqualTo("Information Technology");
        assertThat(dept.getDescription()).isEqualTo("Updated description");
    }

    @Test
    void should_not_update_deleted_department() {
        var dept = DepartmentAggregate.create("Finance", "Finance dept", "system");
        dept.softDelete("admin");

        assertThatThrownBy(() -> dept.update("Finance Updated", "desc", "admin"))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void should_validate_name_not_blank() {
        assertThatThrownBy(() -> DepartmentAggregate.create("", "desc", "system"))
            .isInstanceOf(IllegalArgumentException.class);
    }
}
