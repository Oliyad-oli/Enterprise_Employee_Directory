package com.act.intern.employeedirectory.enterprise.domain.employee.valueobject;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.*;

class SalaryTest {

    @Test
    void should_create_valid_salary() {
        var salary = Salary.of(BigDecimal.valueOf(50000));
        assertThat(salary.amount()).isEqualByComparingTo(BigDecimal.valueOf(50000));
    }

    @Test
    void should_reject_zero_salary() {
        assertThatThrownBy(() -> Salary.of(BigDecimal.ZERO))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("positive");
    }

    @Test
    void should_reject_negative_salary() {
        assertThatThrownBy(() -> Salary.of(BigDecimal.valueOf(-1000)))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_reject_salary_exceeding_maximum() {
        assertThatThrownBy(() -> Salary.of(BigDecimal.valueOf(100_000_001)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("maximum");
    }

    @Test
    void should_check_salary_in_range() {
        var salary = Salary.of(BigDecimal.valueOf(60000));
        assertThat(salary.isInRange(Salary.of(BigDecimal.valueOf(50000)), Salary.of(BigDecimal.valueOf(70000)))).isTrue();
        assertThat(salary.isInRange(Salary.of(BigDecimal.valueOf(70000)), Salary.of(BigDecimal.valueOf(90000)))).isFalse();
    }

    @Test
    void should_create_via_factory_method() {
        var s1 = Salary.of(BigDecimal.valueOf(45000));
        var s2 = Salary.of(BigDecimal.valueOf(45000));
        assertThat(s1).isEqualTo(s2);
    }
}
