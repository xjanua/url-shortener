package me.xjanua.spring.backend.dto.shortLink;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShortLinkResponseSummaryDto {
    private Long id;
    private String title;
    private String shortUrl;
    private String originalUrl;
    private Long clickCount;
    private Long uniqueClicks;
    private LocalDateTime createdAt;
}