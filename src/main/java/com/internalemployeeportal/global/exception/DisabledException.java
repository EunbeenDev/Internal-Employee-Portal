package com.internalemployeeportal.global.exception;

import com.internalemployeeportal.global.payload.ErrorCode;

public class DisabledException extends BusinessException{
    public DisabledException(ErrorCode errorCode) {
        super(errorCode);
    }
}
