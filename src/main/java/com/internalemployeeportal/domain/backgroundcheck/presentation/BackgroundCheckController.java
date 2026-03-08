package com.internalemployeeportal.domain.backgroundcheck.presentation;

import com.internalemployeeportal.domain.backgroundcheck.application.BackgroundCheckService;
import com.internalemployeeportal.domain.backgroundcheck.dto.response.BackgroundCheckCreatedRes;
import com.internalemployeeportal.domain.backgroundcheck.dto.response.BackgroundCheckPendingRes;
import com.internalemployeeportal.domain.backgroundcheck.dto.response.BackgroundCheckResultRes;
import com.internalemployeeportal.global.payload.ErrorResponse;
import com.internalemployeeportal.global.payload.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/background-checks")
public class BackgroundCheckController {

    private final BackgroundCheckService backgroundCheckService;


    @Operation(summary = "배경 조사 요청 API", description = "직원에 대한 Background Check를 요청하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Background Check 요청 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "Background Check 요청 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{employeeId}")
    public ResponseEntity<?> requestBackgroundCheck(@PathVariable Long employeeId) {
        return backgroundCheckService.requestBackgroundCheck(employeeId);

    }

    @Operation(summary = "Background Check 결과 조회 API", description = "직원에 대한 Background Check 결과를 조회하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Background Check 결과 조회 성공(clear, flagged)", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BackgroundCheckResultRes.class) ) } ),
            @ApiResponse(responseCode = "200", description = "Background Check 결과 조회 성공(pending)", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BackgroundCheckPendingRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "Background Check 결과 조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{employeeCode}")
    public ResponseEntity<?> getBackgroundCheckResult(@PathVariable String employeeCode) {
        return backgroundCheckService.getBackgroundCheckResult(employeeCode);

    }


}