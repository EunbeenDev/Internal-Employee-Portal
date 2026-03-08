package com.internalemployeeportal.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LocalSignInReq {

    @Schema(type = "string", example = "employeee1", description = "사용자 계정 ID")
    @NotBlank(message = "사용자 계정 ID는 필수입니다.")
    private String accountId;

    @Schema(type = "string", example = "password123!", description = "비밀번호")
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
}

