package com.internalemployeeportal.global.exception;

import com.internalemployeeportal.global.payload.ErrorCode;
import lombok.Getter;

@Getter
public class DuplicateException extends BusinessException {
    public DuplicateException(ErrorCode errorCode) {
        super(errorCode);
    }
}
