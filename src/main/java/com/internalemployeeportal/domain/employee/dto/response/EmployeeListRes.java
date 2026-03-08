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
public class EmployeeListRes {

    @Schema(description = "직원 코드", example = "EMP-2026-001")
    private String employeeCode;

    @Schema(description = "직원 이름", example = "홍길동")
    private String name;

    @Schema(description = "소속 부서", example = "인사팀")
    private String department;
}
