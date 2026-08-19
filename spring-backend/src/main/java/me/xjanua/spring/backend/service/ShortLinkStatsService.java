package me.xjanua.spring.backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.dto.shortLink.RecentClickActivityDto;
import me.xjanua.spring.backend.dto.shortLink.ShortLinkStatsDto;
import me.xjanua.spring.backend.dto.shortLink.TopCountryDto;
import me.xjanua.spring.backend.model.ClickEvent;
import me.xjanua.spring.backend.model.ShortLink;
import me.xjanua.spring.backend.repository.ClickEventRepository;
import me.xjanua.spring.backend.util.SecurityUtil;

@Service
@RequiredArgsConstructor
public class ShortLinkStatsService {

    private static final int RECENT_ACTIVITY_LIMIT = 10;

    private final ShortLinkService shortLinkService;
    private final ClickEventRepository clickEventRepository;

    @Value("${short-link.base-url}")
    private String shortLinkBaseUrl;

    @Transactional(readOnly = true)
    public ShortLinkStatsDto getStats(Long shortLinkId) {
        ShortLink shortLink = shortLinkService.findById(shortLinkId);
        UUID currentUserId = SecurityUtil.getCurrentUserId();

        if (!shortLinkService.isOwner(shortLink.getOwner().getId(), currentUserId)) {
            throw new AccessDeniedException("Bạn không có quyền xem thống kê short link này");
        }

        ShortLinkStatsDto response = new ShortLinkStatsDto();
        response.setId(shortLink.getId());
        response.setOriginalUrl(shortLink.getOriginalUrl());
        response.setShortUrl(shortLinkBaseUrl + "/" + shortLink.getShortCode());
        response.setClicks(shortLink.getClickCount());
        response.setUniqueClicks(shortLink.getUniqueClicks());
        response.setTopCountry(findTopCountry(shortLinkId));
        response.setTopReferrer(findTopReferrer(shortLinkId));
        response.setRecentActivities(findRecentActivities(shortLinkId));

        return response;
    }

    private TopCountryDto findTopCountry(Long shortLinkId) {
        return clickEventRepository.findTopCountriesByShortLinkId(shortLinkId, PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElse(null);
    }

    private String findTopReferrer(Long shortLinkId) {
        return clickEventRepository.findTopReferrersByShortLinkId(shortLinkId, PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .orElse(null);
    }

    private List<RecentClickActivityDto> findRecentActivities(Long shortLinkId) {
        return clickEventRepository
                .findByShortLink_IdAndIsBotFalseOrderByClickedAtDesc(shortLinkId, PageRequest.of(0, RECENT_ACTIVITY_LIMIT))
                .stream()
                .map(this::toRecentActivity)
                .toList();
    }

    private RecentClickActivityDto toRecentActivity(ClickEvent clickEvent) {
        RecentClickActivityDto activity = new RecentClickActivityDto();
        activity.setCountryCode(clickEvent.getCountryCode());
        activity.setDeviceType(clickEvent.getDeviceType());
        activity.setOperatingSystem(clickEvent.getOperatingSystem());
        activity.setBrowser(clickEvent.getBrowser());
        activity.setReferrer(normalizeReferrer(clickEvent.getReferrer()));
        activity.setClickedAt(clickEvent.getClickedAt());
        return activity;
    }

    private String normalizeReferrer(String referrer) {
        return referrer == null || referrer.isBlank() ? "Direct" : referrer;
    }
}
