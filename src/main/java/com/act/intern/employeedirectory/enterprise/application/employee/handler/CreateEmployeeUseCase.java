package com.act.intern.employeedirectory.enterprise.application.employee.handler;

import com.act.intern.employeedirectory.enterprise.application.employee.command.CreateEmployeeCommand;

public interface CreateEmployeeUseCase {
    Long handle(CreateEmployeeCommand command);
}
