package com.internalemployeeportal.domain.employee.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyInfoRes {
    @Schema(description = "직원 코드", example = "EMP-2026-001")
    private String employeeCode;

    @Schema(description = "직원 이름", example = "홍길동")
    private String name;

    @Schema(description = "직원 이메일", example = "test@gmail.com")
    private String email;

    @Schema(description = "직원 생년월일", example = "1990-01-01")
    private String dateOfBirth;

    @Schema(description = "소속 부서", example = "인사팀")
    private String departmentName;
}
