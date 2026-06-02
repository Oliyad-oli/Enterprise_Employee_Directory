-- Enterprise Departments Table (DDD + Hexagonal Architecture)
CREATE TABLE ent_departments (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    deleted     BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at  TIMESTAMP,
    version     INTEGER NOT NULL DEFAULT 0,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(100) NOT NULL DEFAULT 'system',
    updated_by  VARCHAR(100) NOT NULL DEFAULT 'system',
    CONSTRAINT uq_ent_dept_name_active UNIQUE (name)
);

CREATE INDEX ix_ent_dept_deleted ON ent_departments(deleted);
CREATE INDEX ix_ent_dept_name ON ent_departments(name);
