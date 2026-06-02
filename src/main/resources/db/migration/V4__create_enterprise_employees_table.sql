-- Enterprise Employees Table (DDD + Hexagonal Architecture)
CREATE TABLE ent_employees (
    id            BIGSERIAL PRIMARY KEY,
    first_name    VARCHAR(100) NOT NULL,
    last_name     VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL,
    salary        DECIMAL(15, 2) NOT NULL,
    hire_date     DATE NOT NULL,
    status        VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    department_id BIGINT NOT NULL,
    deleted       BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at    TIMESTAMP,
    version       INTEGER NOT NULL DEFAULT 0,
    created_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by    VARCHAR(100) NOT NULL DEFAULT 'system',
    updated_by    VARCHAR(100) NOT NULL DEFAULT 'system',

    CONSTRAINT uq_ent_emp_email UNIQUE (email),
    CONSTRAINT fk_ent_emp_department
        FOREIGN KEY (department_id) REFERENCES ent_departments(id)
);

CREATE INDEX ix_ent_emp_department ON ent_employees(department_id);
CREATE INDEX ix_ent_emp_deleted ON ent_employees(deleted);
CREATE INDEX ix_ent_emp_email ON ent_employees(email);
CREATE INDEX ix_ent_emp_salary ON ent_employees(salary);
CREATE INDEX ix_ent_emp_status ON ent_employees(status);
