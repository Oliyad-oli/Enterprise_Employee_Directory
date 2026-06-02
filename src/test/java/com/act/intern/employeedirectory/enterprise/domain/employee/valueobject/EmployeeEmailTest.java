package com.act.intern.employeedirectory.enterprise.domain.employee.valueobject;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class EmployeeEmailTest {

    @Test
    void should_create_valid_email() {
        var email = EmployeeEmail.of("test@example.com");
        assertThat(email.value()).isEqualTo("test@example.com");
    }

    @Test
    void should_normalise_email_to_lowercase() {
        var email = EmployeeEmail.of("Test@EXAMPLE.COM");
        assertThat(email.value()).isEqualTo("test@example.com");
    }

    @Test
    void should_reject_invalid_email() {
        assertThatThrownBy(() -> EmployeeEmail.of("not-valid"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_reject_null_email() {
        assertThatThrownBy(() -> EmployeeEmail.of(null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void emails_with_same_value_should_be_equal() {
        assertThat(EmployeeEmail.of("a@b.com")).isEqualTo(EmployeeEmail.of("A@B.COM"));
    }
}
