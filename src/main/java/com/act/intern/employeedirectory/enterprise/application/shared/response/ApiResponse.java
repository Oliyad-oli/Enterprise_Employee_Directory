package com.act.intern.employeedirectory.enterprise.application.shared.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
    boolean success,
    String message,
    LocalDateTime timestamp,
    T data,
    Object errors
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Operation completed successfully", LocalDateTime.now(), data, null);
    }
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, LocalDateTime.now(), data, null);
    }
    public static <T> ApiResponse<T> error(String message, Object errors) {
        return new ApiResponse<>(false, message, LocalDateTime.now(), null, errors);
    }
    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(true, "Resource created successfully", LocalDateTime.now(), data, null);
    }
}
