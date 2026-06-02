CREATE TABLE employees (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    salary DECIMAL(15, 2) NOT NULL,
    hire_date DATE NOT NULL,
    department_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_employee_department
    FOREIGN KEY (department_id)
    REFERENCES departments(id)
);

CREATE INDEX ix_employee_department
ON employees(department_id);
