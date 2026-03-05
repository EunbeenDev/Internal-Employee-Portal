package com.internalemployeeportal.global.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;


@Getter
@Schema(description = "API 표준 응답")
public class CommonApiResponse<T> { // Generic Type을 사용하여 다양한 타입의 응답을 처리할 수 있도록 함

    @Schema(example = "true", description = "요청 성공 여부")
    private final boolean check;

    @Schema(description = "응답 정보 (데이터 또는 메시지)")
    private final T information;

    @Builder
    public CommonApiResponse(boolean check, T information) {
        this.check = check;
        this.information = information;
    }

    public static <T> CommonApiResponse<T> success(T information) {
        return new CommonApiResponse<>(true, information);
    }

    public static <T> CommonApiResponse<T> fail(T information) {
        return new CommonApiResponse<>(false, information);
    }
}
