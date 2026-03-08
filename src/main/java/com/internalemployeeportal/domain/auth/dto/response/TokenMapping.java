package com.internalemployeeportal.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TokenMapping {
    private String accountId;
    private String accessToken;
    private String refreshToken;
}
