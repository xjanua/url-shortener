package me.xjanua.spring.backend.dto.shortLink;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import me.xjanua.spring.backend.enums.ShortCodeType;
import me.xjanua.spring.backend.enums.ShortLinkStatus;

@Getter
@Setter
public class ShortLinkResponse {
    private Long id;
    private String title;
    private String originalUrl;
    private String androidUrl;
    private String iosUrl;
    private String desktopUrl;
    private String shortCode;
    private ShortCodeType shortCodeType;
    private String shortUrl;
    private ShortLinkStatus status;
    private LocalDateTime expiresAt;
    private Long clickCount;
    private Long uniqueClicks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
