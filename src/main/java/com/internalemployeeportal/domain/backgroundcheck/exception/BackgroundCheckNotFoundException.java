package com.internalemployeeportal.domain.backgroundcheck.exception;

import com.internalemployeeportal.global.exception.NotFoundException;
import com.internalemployeeportal.global.payload.ErrorCode;

public class BackgroundCheckNotFoundException extends NotFoundException {
    public BackgroundCheckNotFoundException() {
        super(ErrorCode.BACKGROUND_CHECK_NOT_FOUND);
    }
}
