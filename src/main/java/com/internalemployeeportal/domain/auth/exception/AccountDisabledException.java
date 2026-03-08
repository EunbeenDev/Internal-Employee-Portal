package com.internalemployeeportal.domain.auth.exception;

import com.internalemployeeportal.global.exception.DisabledException;
import com.internalemployeeportal.global.payload.ErrorCode;

public class AccountDisabledException extends DisabledException{
    public AccountDisabledException() {
        super(ErrorCode.ACCOUNT_DISABLED);
    }
}
