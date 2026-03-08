package com.internalemployeeportal.global.exception;

import com.internalemployeeportal.global.payload.ErrorCode;
import lombok.Getter;

@Getter
public class AuthorizedException extends BusinessException {
    public AuthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
