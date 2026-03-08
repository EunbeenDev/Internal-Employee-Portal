package com.internalemployeeportal.domain.user.exception;

import com.internalemployeeportal.global.exception.NotFoundException;
import com.internalemployeeportal.global.payload.ErrorCode;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }

}
