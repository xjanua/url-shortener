package me.xjanua.spring.backend.dto.clickEvent;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ClickEventCreateDto {
    private Long shortLinkId;
    private LocalDateTime clickedAt;
    private String clientIp;
    private String ipHash;
    private String userAgent;
    private String referrer;
    private String countryCode;
    private String deviceType;
    private String browser;
    private String operatingSystem;
    private Boolean isBot;
}
