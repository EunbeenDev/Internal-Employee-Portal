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
public class BackgroundCheckCreatedRes {

    @Schema(description = "Background Check ID", example = "CHK-a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String checkId;

    @Schema(description = "직원 ID", example = "EMP-2024-001")
    private String employeeId;

    @Schema(description = "Background Check 결과 상태", example = "pending/clear/flagged")
    private String status;

    @Schema(description = "Background Check 요청 생성 시간", example = "2025-01-15T09:30:00Z")
    private String createdAt;

}
