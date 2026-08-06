package me.xjanua.spring.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.xjanua.spring.backend.dto.IpInfoResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class IpInfoService {

    private final RestClient restClient;

    @Value("${ipinfo.base-url}")
    private String baseUrl;

    @Value("${ipinfo.access-token}")
    private String accessToken;

    public IpInfoResponse getIpInfo(String ip) {
        try {
            // log.info("Calling IpInfo API for IP: {}", ip);
            return restClient.get()
                    .uri(baseUrl + "/lite/{ip}", ip)
                    .headers(headers -> headers.setBearerAuth(accessToken))
                    .retrieve()
                    .body(IpInfoResponse.class);
        } catch (RestClientException e) {
            log.error("Failed to get IP info for {}: {}", ip, e.getMessage(), e);
            return null;
        }
    }

    public String getCountryCode(String ip) {
        IpInfoResponse response = getIpInfo(ip);
        return response != null ? response.getCountryCode() : null;
    }
}