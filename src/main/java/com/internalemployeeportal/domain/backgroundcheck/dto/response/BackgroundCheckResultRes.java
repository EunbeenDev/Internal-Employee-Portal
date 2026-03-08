package com.internalemployeeportal.domain.backgroundcheck.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BackgroundCheckResultRes {
    @Schema(description = "Background Check ID", example = "CHK-a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String checkId;

    @Schema(description = "직원 ID", example = "EMP-2024-001")
    private String employeeId;

    @Schema(description = "직원 이름(firstName + lastName)", example = "홍길동")
    private String employeeName;

    @Schema(description = "직원 생년월일", example = "1990-03-15")
    private String dateOfBirth;

    @Schema(description = "Background Check 결과 상태", example = "clear/pending/flagged")
    private String status;

    @Schema(description = "범죄 기록 여부", example = "false")
    private Boolean criminalRecord;

    @Schema(description = "학력 검증 여부", example = "true")
    private Boolean educationVerified;

    @Schema(description = "고용 검증 여부", example = "true")
    private Boolean employmentVerified;

    @Schema(description = "신용 점수", example = "good/fair/poor")
    private String creditScore;

    @Schema(description = "Background Check 요청 생성 시간", example = "2025-01-15T09:30:00Z")
    private String createdAt;

    @Schema(description = "Background Check 완료 시간", example = "2025-01-15T09:31:45Z")
    private String completedAt;

}
