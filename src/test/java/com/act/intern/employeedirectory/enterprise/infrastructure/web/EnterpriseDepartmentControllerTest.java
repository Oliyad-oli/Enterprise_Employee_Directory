package com.act.intern.employeedirectory.enterprise.infrastructure.web;

import com.act.intern.employeedirectory.enterprise.application.department.handler.*;
import com.act.intern.employeedirectory.enterprise.domain.department.aggregate.DepartmentAggregate;
import com.act.intern.employeedirectory.enterprise.domain.department.exception.DepartmentNotFoundException;
import com.act.intern.employeedirectory.enterprise.domain.shared.valueobject.AuditInfo;
import com.act.intern.employeedirectory.enterprise.infrastructure.web.department.controller.EnterpriseDepartmentController;
import com.act.intern.employeedirectory.enterprise.infrastructure.web.department.dto.DepartmentResponse;
import com.act.intern.employeedirectory.enterprise.infrastructure.web.department.mapper.DepartmentWebMapper;
import com.act.intern.employeedirectory.enterprise.infrastructure.web.shared.EnterpriseGlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EnterpriseDepartmentControllerTest {

    @Mock private CreateDepartmentUseCase createDepartmentUseCase;
    @Mock private UpdateDepartmentUseCase updateDepartmentUseCase;
    @Mock private DeleteDepartmentUseCase deleteDepartmentUseCase;
    @Mock private GetDepartmentUseCase getDepartmentUseCase;
    @Mock private ListDepartmentsUseCase listDepartmentsUseCase;
    @Mock private DepartmentWebMapper mapper;

    @InjectMocks private EnterpriseDepartmentController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new EnterpriseGlobalExceptionHandler())
            .build();
    }

    @Test
    void should_return_404_when_department_not_found() throws Exception {
        when(getDepartmentUseCase.handle(any())).thenThrow(new DepartmentNotFoundException(999L));

        mockMvc.perform(get("/api/v2/enterprise/departments/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void should_return_all_departments() throws Exception {
        var dept = DepartmentAggregate.reconstitute(1L, "Engineering", "Desc",
            AuditInfo.createNew("system"), false, 0);
        when(listDepartmentsUseCase.handle(any())).thenReturn(List.of(dept));
        when(mapper.toResponse(any())).thenReturn(
            new DepartmentResponse(1L, "Engineering", "Desc", LocalDateTime.now(), LocalDateTime.now(), "system")
        );

        mockMvc.perform(get("/api/v2/enterprise/departments"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data[0].name").value("Engineering"));
    }

    @Test
    void should_return_400_on_blank_name() throws Exception {
        mockMvc.perform(post("/api/v2/enterprise/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"\"}"))
            .andExpect(status().isBadRequest());
    }
}
