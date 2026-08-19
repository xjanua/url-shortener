package me.xjanua.spring.backend.dto.shortLink;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecentClickActivityDto {

    private String countryCode;
    private String deviceType;
    private String operatingSystem;
    private String browser;
    private String referrer;
    private LocalDateTime clickedAt;
}
