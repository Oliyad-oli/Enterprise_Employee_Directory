package com.act.intern.employeedirectory.enterprise.infrastructure.web.shared;

import com.act.intern.employeedirectory.enterprise.application.shared.response.ApiResponse;
import com.act.intern.employeedirectory.enterprise.domain.department.exception.DepartmentNotFoundException;
import com.act.intern.employeedirectory.enterprise.domain.department.exception.DuplicateDepartmentNameException;
import com.act.intern.employeedirectory.enterprise.domain.employee.exception.DuplicateEmployeeEmailException;
import com.act.intern.employeedirectory.enterprise.domain.employee.exception.EmployeeNotFoundException;
import com.act.intern.employeedirectory.enterprise.domain.employee.exception.InvalidEmployeeStateException;
import com.act.intern.employeedirectory.enterprise.domain.shared.exception.DomainException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestControllerAdvice(basePackages = "com.act.intern.employeedirectory.enterprise.infrastructure.web")
@Slf4j
public class EnterpriseGlobalExceptionHandler {

    @ExceptionHandler({EmployeeNotFoundException.class, DepartmentNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ProblemDetail handleNotFound(DomainException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("Resource Not Found");
        pd.setType(URI.create("https://api.employee-directory.com/errors/not-found"));
        pd.setProperty("errorCode", ex.getErrorCode());
        pd.setProperty("timestamp", LocalDateTime.now());
        return pd;
    }

    @ExceptionHandler({DuplicateEmployeeEmailException.class, DuplicateDepartmentNameException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ProblemDetail handleConflict(DomainException ex) {
        log.warn("Conflict: {}", ex.getMessage());
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setTitle("Resource Conflict");
        pd.setType(URI.create("https://api.employee-directory.com/errors/conflict"));
        pd.setProperty("errorCode", ex.getErrorCode());
        pd.setProperty("timestamp", LocalDateTime.now());
        return pd;
    }

    @ExceptionHandler(InvalidEmployeeStateException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ProblemDetail handleInvalidState(DomainException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        pd.setTitle("Invalid State");
        pd.setProperty("errorCode", ex.getErrorCode());
        pd.setProperty("timestamp", LocalDateTime.now());
        return pd;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        pd.setTitle("Bad Request");
        pd.setProperty("timestamp", LocalDateTime.now());
        return pd;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleValidation(MethodArgumentNotValidException ex) {
        List<Map<String, String>> errors = ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> Map.of("field", fe.getField(), "message", fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Invalid"))
            .toList();
        return ApiResponse.error("Validation failed", errors);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ProblemDetail handleGeneral(Exception ex) {
        log.error("Unexpected error", ex);
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        pd.setTitle("Internal Server Error");
        pd.setProperty("timestamp", LocalDateTime.now());
        return pd;
    }
}
