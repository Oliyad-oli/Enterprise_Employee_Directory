package com.act.intern.employeedirectory.enterprise.infrastructure.web.employee.controller;

import com.act.intern.employeedirectory.enterprise.application.employee.command.*;
import com.act.intern.employeedirectory.enterprise.application.employee.handler.*;
import com.act.intern.employeedirectory.enterprise.application.employee.query.*;
import com.act.intern.employeedirectory.enterprise.application.shared.response.ApiResponse;
import com.act.intern.employeedirectory.enterprise.infrastructure.web.employee.dto.*;
import com.act.intern.employeedirectory.enterprise.infrastructure.web.employee.mapper.EmployeeWebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v2/enterprise/employees")
@RequiredArgsConstructor
@Tag(name = "Enterprise Employees", description = "DDD + Hexagonal Architecture Employee API")
public class EnterpriseEmployeeController {

    private final CreateEmployeeUseCase createEmployeeUseCase;
    private final UpdateEmployeeUseCase updateEmployeeUseCase;
    private final DeleteEmployeeUseCase deleteEmployeeUseCase;
    private final GetEmployeeUseCase getEmployeeUseCase;
    private final ListEmployeesUseCase listEmployeesUseCase;
    private final EmployeeWebMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new employee")
    public ApiResponse<EmployeeResponse> createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
        Long id = createEmployeeUseCase.handle(new CreateEmployeeCommand(
            request.firstName(), request.lastName(), request.email(),
            request.salary(), request.hireDate(), request.departmentId(), "system"
        ));
        var employee = getEmployeeUseCase.handle(new GetEmployeeQuery(id));
        return ApiResponse.created(mapper.toResponse(employee));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an employee")
    public ApiResponse<EmployeeResponse> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEmployeeRequest request) {
        updateEmployeeUseCase.handle(new UpdateEmployeeCommand(
            id, request.firstName(), request.lastName(), request.email(),
            request.salary(), request.hireDate(), request.departmentId(), "system"
        ));
        var employee = getEmployeeUseCase.handle(new GetEmployeeQuery(id));
        return ApiResponse.success("Employee updated successfully", mapper.toResponse(employee));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an employee (soft delete)")
    public void deleteEmployee(@PathVariable Long id) {
        deleteEmployeeUseCase.handle(new DeleteEmployeeCommand(id, "system"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get employee by ID")
    public ApiResponse<EmployeeResponse> getEmployee(@PathVariable Long id) {
        var employee = getEmployeeUseCase.handle(new GetEmployeeQuery(id));
        return ApiResponse.success(mapper.toResponse(employee));
    }

    @GetMapping
    @Operation(summary = "List employees with optional department filter and pagination")
    public ApiResponse<List<EmployeeResponse>> listEmployees(
            @RequestParam(required = false) Long departmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        List<EmployeeResponse> employees;
        if (departmentId != null) {
            employees = listEmployeesUseCase.handle(new ListEmployeesQuery(departmentId, page, size, sortBy))
                .stream().map(mapper::toResponse).toList();
        } else {
            employees = listEmployeesUseCase.handle(new ListEmployeesQuery(null, page, size, sortBy))
                .stream().map(mapper::toResponse).toList();
        }
        return ApiResponse.success(employees);
    }

    @GetMapping("/search")
    @Operation(summary = "Search employees by keyword")
    public ApiResponse<List<EmployeeResponse>> searchEmployees(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        var employees = listEmployeesUseCase.handle(new SearchEmployeesQuery(keyword, null, null, page, size, sortBy))
            .stream().map(mapper::toResponse).toList();
        return ApiResponse.success(employees);
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter employees by salary range")
    public ApiResponse<List<EmployeeResponse>> filterBySalary(
            @RequestParam BigDecimal minSalary,
            @RequestParam BigDecimal maxSalary,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "salary") String sortBy) {
        var employees = listEmployeesUseCase.handle(new SearchEmployeesQuery(null, minSalary, maxSalary, page, size, sortBy))
            .stream().map(mapper::toResponse).toList();
        return ApiResponse.success(employees);
    }
}
