package com.internalemployeeportal.global.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "API 오류 응답 포맷")
public class ErrorResponse {

    @Schema(description = "HTTP 상태 코드", example = "404")
    private int status;

    @Schema(description = "에러 메시지", example = "존재하지 않는 사용자입니다.")
    private String message;

    @Schema(description = "비즈니스 에러 코드", example = "U002")
    private String code;

    private ErrorResponse(ErrorCode errorCode) {
        this.status = errorCode.getStatus();
        this.message = errorCode.getMessage();
        this.code = errorCode.getCode();
    }

    private ErrorResponse(ErrorCode errorCode, String customMessage) {
        this.status = errorCode.getStatus();
        this.message = customMessage;
        this.code = errorCode.getCode();
    }

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode);
    }

    public static ErrorResponse of(ErrorCode errorCode, String customMessage) {
        return new ErrorResponse(errorCode, customMessage);
    }
}