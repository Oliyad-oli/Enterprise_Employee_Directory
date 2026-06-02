package com.act.intern.employeedirectory.enterprise.application.employee.handler;

import com.act.intern.employeedirectory.enterprise.application.employee.command.DeleteEmployeeCommand;

public interface DeleteEmployeeUseCase {
    void handle(DeleteEmployeeCommand command);
}
