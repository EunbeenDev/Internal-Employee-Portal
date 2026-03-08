package com.internalemployeeportal.domain.auth.exception;

import com.internalemployeeportal.global.exception.AuthenticationException;
import com.internalemployeeportal.global.payload.ErrorCode;

public class AccountIdNotFoundException extends AuthenticationException {
    public AccountIdNotFoundException() {
        super(ErrorCode.ACCOUNT_ID_NOT_FOUND);
    }
}
