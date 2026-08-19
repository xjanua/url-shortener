package me.xjanua.spring.backend.dto.shortLink;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import me.xjanua.spring.backend.enums.ShortCodeType;
import me.xjanua.spring.backend.enums.ShortLinkStatus;

@Getter
@Setter
public class ShortLinkResponseSummaryDto {
    private Long id;
    private String title;
    private String shortUrl;
    private ShortCodeType shortCodeType;
    private ShortLinkStatus status;
    private String originalUrl;
    private Long clickCount;
    private Long uniqueClicks;
    private LocalDateTime createdAt;
}
