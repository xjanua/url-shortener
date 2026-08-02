package me.xjanua.spring.backend.dto.clickEvent;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import me.xjanua.spring.backend.model.ShortLink;

@Getter
@Builder
public class ClickEventCreateDto {
    private ShortLink shortLink;
    private LocalDateTime clickedAt;
    private String ipHash;
    private String userAgent;
    private String referrer;
    private String countryCode;
    private String deviceType;
    private String browser;
    private String operatingSystem;
    private Boolean isBot;
}