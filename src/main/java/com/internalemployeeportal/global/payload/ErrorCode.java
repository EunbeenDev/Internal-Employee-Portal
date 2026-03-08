package com.internalemployeeportal.global.payload;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // Server (SVR)
    INTERNAL_SERVER_ERROR(500, "SVR001", "서버 내부 오류입니다."),

    INVALID_PARAMETER(400, "E001", "잘못된 요청 데이터 입니다."),
    INVALID_REPRESENTATION(400, "E002", "잘못된 표현 입니다."),
    INVALID_FILE_PATH(400, "E003", "잘못된 파일 경로 입니다."),
    INVALID_OPTIONAL_ISPRESENT(400, "E004", "해당 값이 존재하지 않습니다."),
    INVALID_CHECK(400, "E005", "해당 값이 유효하지 않습니다."),
    INVALID_AUTHENTICATION(400, "E006", "잘못된 인증입니다."),
    ACCOUNT_ID_NOT_FOUND(400, "E007", "해당 계정 ID를 찾을 수 없습니다."),
    INVALID_TOKEN(400, "E007", "잘못된 토큰입니다."),
    NOT_FOUND(404, "E008", "해당 데이터를 찾을 수 없습니다."),
    DUPLICATE_ERROR(409, "E009", "중복된 데이터가 존재합니다."),

    ACCOUNT_DISABLED(403, "E010", "사용이 불가한 계정입니다."),

    // User (USR)
    USER_DUPLICATE(409, "USR002", "이미 존재하는 사용자입니다."),
    USER_NOT_FOUND(404, "USR001", "해당 사용자를 찾을 수 없습니다."),
    LOGIN_FAILED(400, "USR003", "로그인에 실패하였습니다. 아이디와 비밀번호를 확인해주세요.");

    private final String code;
    private final String message;
    private final int status;

    ErrorCode(final int status, final String code, final String message) {
        this.status = status;
        this.message = message;
        this.code = code;
    }

}