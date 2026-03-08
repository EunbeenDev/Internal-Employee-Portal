package com.internalemployeeportal.domain.employee.presentation;

import com.internalemployeeportal.domain.employee.application.EmployeeService;
import com.internalemployeeportal.domain.employee.dto.request.MyInfoUpdateReq;
import com.internalemployeeportal.domain.employee.dto.response.EmployeeListRes;
import com.internalemployeeportal.domain.employee.dto.response.MyInfoRes;
import com.internalemployeeportal.global.config.security.token.CurrentUser;
import com.internalemployeeportal.global.config.security.token.UserPrincipal;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Employee", description = "Employee 정보 관련 API")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Operation(summary = "직원 목록 조회 API", description = "관리자가 직원 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "직원 목록 조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(type = "array", implementation = EmployeeListRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "직원 목록 조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ADMIN')") // ADMIN 권한이 있는 사용자만 접근 가능
    @GetMapping("/admin/employees")
    public ResponseEntity<?> getEmployeeList() {
        return employeeService.getEmployeeList();
    }


    // 직원 개인 정보 조회 API
    @Operation(summary = "직원 개인 정보 조회 API", description = "직원이 본인의 개인 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "개인 정보 조회 성공", content = { @Content(mediaType = "application/json", schema = @Schema(type = "array", implementation = MyInfoRes.class) ) } ),
            @ApiResponse(responseCode = "400", description = "개인 정보 조회 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('USER')") // USER 권한이 있는 사용자만 접근 가능
    @GetMapping("/api/v1/me")
    public ResponseEntity<?> getMyInfo(@CurrentUser UserPrincipal userPrincipal) {return employeeService.getMyInfo(userPrincipal);}

    // 직원 개인 정보 수정 API
    @Operation(summary = "직원 개인 정보 수정 API", description = "직원이 본인의 개인 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "개인 정보 수정 성공", content = { @Content(mediaType = "application/json", schema = @Schema(type = "array", implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "개인 정보 수정 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('USER')") // USER 권한이 있는 사용자만 접근 가능
    @PatchMapping("/api/v1/me")
    public ResponseEntity<?> updateMyInfo(@CurrentUser UserPrincipal userPrincipal, @RequestBody MyInfoUpdateReq myInfoUpdateReq) {
        return employeeService.updateMyInfo(userPrincipal, myInfoUpdateReq);
    }
}
