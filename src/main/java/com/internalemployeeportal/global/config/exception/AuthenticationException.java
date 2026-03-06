package com.internalemployeeportal.global.config.exception;


import com.internalemployeeportal.global.payload.ErrorCode;
import lombok.Getter;

@Getter
public class AuthenticationException extends org.springframework.security.core.AuthenticationException {

    private ErrorCode errorCode;

    public AuthenticationException(String msg, Throwable t) {
        super(msg, t);
        this.errorCode = ErrorCode.INVALID_REPRESENTATION;
    }

    public AuthenticationException(String msg) {
        super(msg);
    }

    public AuthenticationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}