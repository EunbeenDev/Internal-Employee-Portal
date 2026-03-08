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
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/background-checks")
@Tag(name = "Background Check", description = "Background Check 관련 API")
public class BackgroundCheckController {

    private final BackgroundCheckService backgroundCheckService;


    @Operation(summary = "배경 조사 요청 API", description = "직원에 대한 Background Check를 요청하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Background Check 요청 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "Background Check 요청 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{employeeCode}")
    public ResponseEntity<?> requestBackgroundCheck(@PathVariable String employeeCode) {
        return backgroundCheckService.requestBackgroundCheck(employeeCode);

    }

    @Operation(summary = "최신 Background Check 결과 조회 API", description = "직원에 대한 가장 최근의 Background Check 결과를 조회하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Background Check 결과 조회 성공(clear, flagged)", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BackgroundCheckResultRes.class) ) } ),
            @ApiResponse(responseCode = "200", description = "Background Check 결과 조회 성공(pending)", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = BackgroundCheckPendingRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "Background Check 결과 조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{employeeCode}")
    public ResponseEntity<?> getBackgroundCheckResult(@PathVariable String employeeCode) {
        return backgroundCheckService.getBackgroundCheckResult(employeeCode);
    }

    // Pending 상태 목록 조회
    @Operation(summary = "모든 Pending 상태의 Background Check 목록 조회 API", description = "Pending 상태인 Background Check를 모두 조회하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pending 상태의 Background Check 목록 조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(type = "array", implementation = BackgroundCheckPendingRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "Pending 상태의 Background Check 목록 조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all/pending")
    public ResponseEntity<?> getPendingBackgroundCheckResult() {
        return backgroundCheckService.getPendingBackgroundCheckResultList();
    }


    // Clear 상태 목록 조회
    @Operation(summary = "background Check history - Clear 상태의 Background Check 목록 조회 API", description = "Clear 상태인 Background Check의 목록을 조회하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clear 상태의 Background Check 목록 조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(type = "array", implementation = BackgroundCheckResultRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "Clear 상태의 Background Check 목록 조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("{employeeCode}/list/clear")
    public ResponseEntity<?> getClearBackgroundCheckResultList(@PathVariable String employeeCode) {
        return backgroundCheckService.getClearBackgroundCheckResultList(employeeCode);
    }

    // Flagged 상태 목록 조회
    @Operation(summary = "background Check history - Flagged 상태의 Background Check 목록 조회 API", description = "Flagged 상태인 Background Check의 목록을 조회하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Flagged 상태의 Background Check 목록 조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(type = "array", implementation = BackgroundCheckResultRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "Flagged 상태의 Background Check 목록 조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("{employeeCode}/list/Flagged")
    public ResponseEntity<?> getFlaggedBackgroundCheckResultList(@PathVariable String employeeCode) {
        return backgroundCheckService.getFlaggedBackgroundCheckResultList(employeeCode);
    }




}