package com.internalemployeeportal.global.exception;

import com.internalemployeeportal.global.payload.ErrorCode;
import lombok.Getter;

@Getter
public class NotFoundException extends BusinessException {
    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
