package com.act.intern.employeedirectory.enterprise.infrastructure.web.department.controller;

import com.act.intern.employeedirectory.enterprise.application.department.command.*;
import com.act.intern.employeedirectory.enterprise.application.department.handler.*;
import com.act.intern.employeedirectory.enterprise.application.department.query.*;
import com.act.intern.employeedirectory.enterprise.application.shared.response.ApiResponse;
import com.act.intern.employeedirectory.enterprise.infrastructure.web.department.dto.*;
import com.act.intern.employeedirectory.enterprise.infrastructure.web.department.mapper.DepartmentWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/enterprise/departments")
@RequiredArgsConstructor
@Tag(name = "Enterprise Departments", description = "DDD + Hexagonal Architecture Department API")
public class EnterpriseDepartmentController {

    private final CreateDepartmentUseCase createDepartmentUseCase;
    private final UpdateDepartmentUseCase updateDepartmentUseCase;
    private final DeleteDepartmentUseCase deleteDepartmentUseCase;
    private final GetDepartmentUseCase getDepartmentUseCase;
    private final ListDepartmentsUseCase listDepartmentsUseCase;
    private final DepartmentWebMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new department")
    public ApiResponse<DepartmentResponse> createDepartment(@Valid @RequestBody CreateDepartmentRequest request) {
        Long id = createDepartmentUseCase.handle(new CreateDepartmentCommand(request.name(), request.description(), "system"));
        var dept = getDepartmentUseCase.handle(new GetDepartmentQuery(id));
        return ApiResponse.created(mapper.toResponse(dept));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a department")
    public ApiResponse<DepartmentResponse> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDepartmentRequest request) {
        updateDepartmentUseCase.handle(new UpdateDepartmentCommand(id, request.name(), request.description(), "system"));
        var dept = getDepartmentUseCase.handle(new GetDepartmentQuery(id));
        return ApiResponse.success("Department updated successfully", mapper.toResponse(dept));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a department (soft delete)")
    public void deleteDepartment(@PathVariable Long id) {
        deleteDepartmentUseCase.handle(new DeleteDepartmentCommand(id, "system"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get department by ID")
    public ApiResponse<DepartmentResponse> getDepartment(@PathVariable Long id) {
        var dept = getDepartmentUseCase.handle(new GetDepartmentQuery(id));
        return ApiResponse.success(mapper.toResponse(dept));
    }

    @GetMapping
    @Operation(summary = "List all departments")
    public ApiResponse<List<DepartmentResponse>> listDepartments() {
        var depts = listDepartmentsUseCase.handle(new ListDepartmentsQuery())
            .stream().map(mapper::toResponse).toList();
        return ApiResponse.success(depts);
    }
}
