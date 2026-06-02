package com.act.intern.employeedirectory.enterprise.application.department.handler;

import com.act.intern.employeedirectory.enterprise.application.department.command.CreateDepartmentCommand;

public interface CreateDepartmentUseCase {
    Long handle(CreateDepartmentCommand command);
}
