package com.internalemployeeportal.domain.backgroundcheck.client;

import com.internalemployeeportal.domain.backgroundcheck.dto.request.BackgroundCheckReq;
import com.internalemployeeportal.domain.backgroundcheck.dto.response.BackgroundCheckCreatedRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class BackgroundCheckClient {

    private final RestClient restClient;

    public BackgroundCheckCreatedRes createBackgroundCheck(BackgroundCheckReq request) {

        return restClient.post()
                .uri("/background-checks")
                .body(request)
                .retrieve()
                .body(BackgroundCheckCreatedRes.class);
    }
}