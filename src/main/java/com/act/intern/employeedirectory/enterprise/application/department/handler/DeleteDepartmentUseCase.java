package com.act.intern.employeedirectory.enterprise.application.department.handler;

import com.act.intern.employeedirectory.enterprise.application.department.command.DeleteDepartmentCommand;

public interface DeleteDepartmentUseCase {
    void handle(DeleteDepartmentCommand command);
}
