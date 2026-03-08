package com.internalemployeeportal.global.exception;


import com.internalemployeeportal.global.payload.ErrorCode;

public class AlreadyExistsException extends BusinessException {
    public AlreadyExistsException(ErrorCode errorCode) {
        super(errorCode);
    }
}
