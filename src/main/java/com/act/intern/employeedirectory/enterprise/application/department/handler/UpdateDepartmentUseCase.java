package com.act.intern.employeedirectory.enterprise.application.department.handler;

import com.act.intern.employeedirectory.enterprise.application.department.command.UpdateDepartmentCommand;

public interface UpdateDepartmentUseCase {
    void handle(UpdateDepartmentCommand command);
}
