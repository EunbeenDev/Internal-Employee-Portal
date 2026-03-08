package com.internalemployeeportal.domain.backgroundcheck.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BackgroundCheckReq {
    private String employeeId;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
}
