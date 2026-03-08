package com.internalemployeeportal.domain.user.exception;

import com.internalemployeeportal.global.exception.DuplicateException;
import com.internalemployeeportal.global.payload.ErrorCode;

public class UserDuplicateException extends DuplicateException {
    public UserDuplicateException() {
        super(ErrorCode.USER_DUPLICATE);
    }
}
