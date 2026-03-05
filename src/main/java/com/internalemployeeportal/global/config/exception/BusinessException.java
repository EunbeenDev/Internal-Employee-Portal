package com.internalemployeeportal.global.config.exception;

import com.internalemployeeportal.global.payload.ErrorCode;
import lombok.Getter;

@Getter
public abstract class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    protected BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
