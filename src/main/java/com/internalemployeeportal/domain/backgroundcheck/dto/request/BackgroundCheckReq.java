package com.internalemployeeportal.domain.backgroundcheck.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BackgroundCheckReq {
    @Schema(description = "직원 코드", example = "EMP-2026-001")
    private String employeeId;

    @Schema(description = "직원 이름", example = "길동")
    private String firstName;

    @Schema(description = "직원 성", example = "홍")
    private String lastName;

    @Schema(description = "직원 생년월일", example = "1990-01-01")
    private String dateOfBirth;
}
