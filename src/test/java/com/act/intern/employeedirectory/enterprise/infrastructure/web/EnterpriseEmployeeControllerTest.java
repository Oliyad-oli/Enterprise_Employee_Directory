package com.act.intern.employeedirectory.enterprise.infrastructure.web;

import com.act.intern.employeedirectory.enterprise.application.employee.handler.*;
import com.act.intern.employeedirectory.enterprise.domain.employee.aggregate.EmployeeAggregate;
import com.act.intern.employeedirectory.enterprise.domain.employee.exception.EmployeeNotFoundException;
import com.act.intern.employeedirectory.enterprise.domain.shared.valueobject.AuditInfo;
import com.act.intern.employeedirectory.enterprise.infrastructure.web.employee.controller.EnterpriseEmployeeController;
import com.act.intern.employeedirectory.enterprise.infrastructure.web.employee.mapper.EmployeeWebMapper;
import com.act.intern.employeedirectory.enterprise.infrastructure.web.shared.EnterpriseGlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EnterpriseEmployeeControllerTest {

    @Mock private CreateEmployeeUseCase createEmployeeUseCase;
    @Mock private UpdateEmployeeUseCase updateEmployeeUseCase;
    @Mock private DeleteEmployeeUseCase deleteEmployeeUseCase;
    @Mock private GetEmployeeUseCase getEmployeeUseCase;
    @Mock private ListEmployeesUseCase listEmployeesUseCase;
    @Mock private EmployeeWebMapper mapper;

    @InjectMocks private EnterpriseEmployeeController controller;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new EnterpriseGlobalExceptionHandler())
            .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void should_return_404_when_employee_not_found() throws Exception {
        when(getEmployeeUseCase.handle(any())).thenThrow(new EmployeeNotFoundException(999L));

        mockMvc.perform(get("/api/v2/enterprise/employees/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void should_return_400_on_invalid_input() throws Exception {
        String invalidJson = """
            {"firstName": "", "lastName": "", "email": "not-an-email", "salary": -100}
            """;

        mockMvc.perform(post("/api/v2/enterprise/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_employee_on_get() throws Exception {
        var emp = EmployeeAggregate.reconstitute(1L, "John", "Doe", "john@example.com",
            BigDecimal.valueOf(50000), LocalDate.now(), "ACTIVE", 1L,
            AuditInfo.createNew("system"), false, 0);

        when(getEmployeeUseCase.handle(any())).thenReturn(emp);
        when(mapper.toResponse(any())).thenReturn(
            new com.act.intern.employeedirectory.enterprise.infrastructure.web.employee.dto.EmployeeResponse(
                1L, "John", "Doe", "John Doe", "john@example.com",
                BigDecimal.valueOf(50000), LocalDate.now(), "ACTIVE", 1L, null, null, "system"
            )
        );

        mockMvc.perform(get("/api/v2/enterprise/employees/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.email").value("john@example.com"));
    }
}
