package com.act.intern.employeedirectory.enterprise.application.employee.handler;

import com.act.intern.employeedirectory.enterprise.application.employee.command.UpdateEmployeeCommand;

public interface UpdateEmployeeUseCase {
    void handle(UpdateEmployeeCommand command);
}
