package me.xjanua.spring.backend.dto.shortLink;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShortLinkStatsDto {
    private Long id;

    private String originalUrl;
    private String shortUrl;

    private Long clicks;
    private Long uniqueClicks;

    private TopCountryDto topCountry;
    private String topReferrer;

    private List<RecentClickActivityDto> recentActivities;
}
