package com.internalemployeeportal.domain.user.presentation;

import com.internalemployeeportal.domain.auth.dto.request.LocalSignInReq;
import com.internalemployeeportal.domain.auth.dto.request.SignUpReq;
import com.internalemployeeportal.domain.auth.dto.response.LoginResponse;
import com.internalemployeeportal.domain.user.application.UserService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User 정보 관리 API")
public class UserController {

    private UserService userService;

    @Operation(summary = "계정 생성 API", description = "관리자가 새로운 직원 계정을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "계정 생성 성공", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Message.class) ) } ),
            @ApiResponse(responseCode = "400", description = "계정 생성 실패", content = { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class) ) } ),
    })
    @PreAuthorize("hasRole('ADMIN')") // ADMIN 권한이 있는 사용자만 접근 가능
    @PostMapping("/account")
    public ResponseEntity<?> createAccount(@Parameter @CurrentUser UserPrincipal userPrincipal, @Valid @RequestBody SignUpReq signUpReq) {
        return userService.createAccount(userPrincipal, signUpReq);
    }


}
