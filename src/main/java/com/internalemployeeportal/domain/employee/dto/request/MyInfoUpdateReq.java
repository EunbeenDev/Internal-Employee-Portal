package com.internalemployeeportal.domain.employee.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyInfoUpdateReq {
    @Schema(description = "직원 이름(firstName)", example = "길동")
    private String firstName;

    @Schema(description = "직원 성(lastName)", example = "홍")
    private String lastName;

    @Schema(description = "직원 이메일", example = "test1@gmail.com")
    private String email;

    @Schema(description = "직원 생년월일", example = "1990-01-01")
    private String dateOfBirth;
}
