package com.internalemployeeportal.domain.auth.domain;


import com.internalemployeeportal.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Token extends BaseEntity {

    @Id
    @Column(name = "account_id", nullable = false)
    private String accountId;

    @Lob
    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    public Token updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        return this;
    }

    @Builder
    public Token(String accountId, String refreshToken) {
        this.accountId = accountId;
        this.refreshToken = refreshToken;
    }
}