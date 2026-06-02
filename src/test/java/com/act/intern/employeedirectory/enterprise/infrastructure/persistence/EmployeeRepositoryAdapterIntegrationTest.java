package com.act.intern.employeedirectory.enterprise.infrastructure.persistence;

import com.act.intern.employeedirectory.enterprise.domain.employee.aggregate.EmployeeAggregate;
import com.act.intern.employeedirectory.enterprise.domain.employee.valueobject.EmployeeEmail;
import com.act.intern.employeedirectory.enterprise.infrastructure.persistence.department.entity.DepartmentJpaEntity;
import com.act.intern.employeedirectory.enterprise.infrastructure.persistence.department.repository.DepartmentJpaRepository;
import com.act.intern.employeedirectory.enterprise.infrastructure.persistence.employee.mapper.EmployeePersistenceMapper;
import com.act.intern.employeedirectory.enterprise.infrastructure.persistence.employee.repository.EmployeeJpaRepository;
import com.act.intern.employeedirectory.enterprise.infrastructure.persistence.employee.repository.EmployeeRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Testcontainers
@Import({EmployeeRepositoryAdapter.class, EmployeePersistenceMapper.class})
class EmployeeRepositoryAdapterIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "false");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired private EmployeeRepositoryAdapter employeeRepository;
    @Autowired private EmployeeJpaRepository employeeJpaRepository;
    @Autowired private DepartmentJpaRepository departmentJpaRepository;

    private Long departmentId;

    @BeforeEach
    void setUp() {
        employeeJpaRepository.deleteAll();
        departmentJpaRepository.deleteAll();
        var dept = departmentJpaRepository.save(DepartmentJpaEntity.builder()
            .name("Engineering").description("Eng dept").build());
        departmentId = dept.getId();
    }

    @Test
    void should_save_and_find_employee() {
        var emp = EmployeeAggregate.create(
            com.act.intern.employeedirectory.enterprise.domain.employee.valueobject.EmployeeName.of("Alice", "Smith"),
            EmployeeEmail.of("alice@test.com"),
            com.act.intern.employeedirectory.enterprise.domain.employee.valueobject.Salary.of(BigDecimal.valueOf(60000)),
            LocalDate.now(), departmentId, "system"
        );

        var saved = employeeRepository.save(emp);
        assertThat(saved.getId()).isNotNull();

        Optional<EmployeeAggregate> found = employeeRepository.findById(saved.getId().value());
        assertThat(found).isPresent();
        assertThat(found.get().getEmail().value()).isEqualTo("alice@test.com");
    }

    @Test
    void should_detect_duplicate_email() {
        var emp = EmployeeAggregate.create(
            com.act.intern.employeedirectory.enterprise.domain.employee.valueobject.EmployeeName.of("Bob", "Jones"),
            EmployeeEmail.of("bob@test.com"),
            com.act.intern.employeedirectory.enterprise.domain.employee.valueobject.Salary.of(BigDecimal.valueOf(55000)),
            LocalDate.now(), departmentId, "system"
        );
        employeeRepository.save(emp);

        boolean exists = employeeRepository.existsByEmail(EmployeeEmail.of("bob@test.com"));
        assertThat(exists).isTrue();
    }

    @Test
    void should_soft_delete_employee() {
        var emp = EmployeeAggregate.create(
            com.act.intern.employeedirectory.enterprise.domain.employee.valueobject.EmployeeName.of("Carol", "White"),
            EmployeeEmail.of("carol@test.com"),
            com.act.intern.employeedirectory.enterprise.domain.employee.valueobject.Salary.of(BigDecimal.valueOf(70000)),
            LocalDate.now(), departmentId, "system"
        );
        var saved = employeeRepository.save(emp);
        saved.softDelete("admin");
        employeeRepository.save(saved);

        Optional<EmployeeAggregate> found = employeeRepository.findById(saved.getId().value());
        assertThat(found).isEmpty(); // soft deleted, findByIdAndDeletedFalse returns empty
    }

    @Test
    void should_search_by_name() {
        var emp = EmployeeAggregate.create(
            com.act.intern.employeedirectory.enterprise.domain.employee.valueobject.EmployeeName.of("David", "Brown"),
            EmployeeEmail.of("david@test.com"),
            com.act.intern.employeedirectory.enterprise.domain.employee.valueobject.Salary.of(BigDecimal.valueOf(80000)),
            LocalDate.now(), departmentId, "system"
        );
        employeeRepository.save(emp);

        var results = employeeRepository.searchByName("dav", 0, 10, "firstName");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName().firstName()).isEqualTo("David");
    }
}
