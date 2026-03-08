package com.internalemployeeportal.domain.user.exception;

import com.internalemployeeportal.global.exception.BusinessException;
import com.internalemployeeportal.global.payload.ErrorCode;

public class AccountIdAlreadyExistsException extends BusinessException {
  public AccountIdAlreadyExistsException() {
    super(ErrorCode.ACCOUNT_ID_ALREADY_EXISTS);
  }
}
