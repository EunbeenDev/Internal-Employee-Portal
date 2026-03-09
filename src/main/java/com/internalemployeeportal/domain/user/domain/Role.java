package com.internalemployeeportal.domain.user.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER"),
    EXECUTIVE("ROLE_EXECUTIVE");

    private String value;
}
