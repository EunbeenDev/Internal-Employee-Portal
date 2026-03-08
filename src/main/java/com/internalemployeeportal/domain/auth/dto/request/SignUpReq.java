package com.internalemployeeportal.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SignUpReq {

    @Schema(type = "string", example = "employeeee1", description = "사용자 계정 ID")
    @NotBlank(message = "계정 ID는 필수입니다.")
    private String accountId;

    @Schema(type = "string", example = "password123!", description = "비밀번호 (8자 이상)")
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;

    @Schema(type = "string", example = "Eunbeen", description = "직원 이름(first name)")
    @NotBlank(message = "이름(first name)은 필수입니다.")
    private String firstName;

    @Schema(type = "string", example = "Noh", description = "직원 이름(last name)")
    @NotBlank(message = "이름(last name)은 필수입니다.")
    private String lastName;

    @Schema(type = "string", example = "2002-01-01", description = "생년월일 (YYYY-MM-DD)")
    @NotBlank(message = "생년월일은 필수입니다.")
    private String dateOfBirth;

    @Schema(type = "string", example = "eunbeen@gmail.com", description = "직원 이메일")
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

}

