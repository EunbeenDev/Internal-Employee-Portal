package com.internalemployeeportal.domain.employee.exception;

import com.internalemployeeportal.global.exception.NotFoundException;
import com.internalemployeeportal.global.payload.ErrorCode;

public class EmployeeNotFoundException extends NotFoundException {
    public EmployeeNotFoundException(){super(ErrorCode.EMPLOYEE_NOT_FOUND);}
}
