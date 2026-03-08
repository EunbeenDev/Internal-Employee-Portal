package com.internalemployeeportal.domain.auth.presentation;

import com.internalemployeeportal.domain.auth.application.AuthService;
import com.internalemployeeportal.domain.auth.dto.request.LocalSignInReq;
import com.internalemployeeportal.domain.auth.dto.response.LoginResponse;
import com.internalemployeeportal.global.config.security.token.CurrentUser;
import com.internalemployeeportal.global.config.security.token.UserPrincipal;
import com.internalemployeeportal.global.payload.ErrorCode;
import com.internalemployeeportal.global.payload.ErrorResponse;
import com.internalemployeeportal.global.payload.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "로그인 관련 API")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "자체 로그인 API", description = "이메일과 비밀번호로 로그인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class) ) } ),
            @ApiResponse(responseCode = "400", description = "로그인 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PostMapping("/login")
    public ResponseEntity<?> localLogin(@Valid @RequestBody LocalSignInReq localSignInReq) {
        return authService.localLogin(localSignInReq);
    }

    @Operation(summary = "로그아웃 API", description = "로그아웃 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "로그아웃 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@Parameter @CurrentUser UserPrincipal userPrincipal) {
        return authService.logout(userPrincipal);
    }

    // TODO: 퇴사 처리 API
    // 직원 퇴사 처리 시, 해당 직원의 토큰을 무효화하는 로직 추가 필요 + soft delete 처리



}