package com.internalemployeeportal.domain.auth.exception;

import com.internalemployeeportal.global.exception.AuthenticationException;
import com.internalemployeeportal.global.payload.ErrorCode;


public class InvalidTokenException extends AuthenticationException {
    public InvalidTokenException() {
        super(ErrorCode.INVALID_TOKEN);
    }
}
