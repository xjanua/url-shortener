package me.xjanua.spring.backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.dto.DailyClickCountDto;
import me.xjanua.spring.backend.dto.clickEvent.ClickEventCreateDto;
import me.xjanua.spring.backend.model.ClickEvent;
import me.xjanua.spring.backend.model.ShortLink;
import me.xjanua.spring.backend.repository.ClickEventRepository;
import me.xjanua.spring.backend.util.SecurityUtil;

@Service
@RequiredArgsConstructor
public class ClickEventService {

    private final ClickEventRepository clickEventRepository;
    private final ShortLinkService shortLinkService;

    public ClickEvent save(ClickEvent clickEvent) {
        return clickEventRepository.save(clickEvent);
    }

    public List<DailyClickCountDto> getCurrentUserDailyClickCounts(LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("from và to không được null");
        }

        if (from.isAfter(to)) {
            throw new IllegalArgumentException("from không được sau to");
        }

        LocalDateTime fromDateTime = from.atStartOfDay();

        LocalDateTime toExclusive = to.plusDays(1).atStartOfDay();
        UUID ownerId = SecurityUtil.getCurrentUserId();

        return clickEventRepository.countClicksByDay(ownerId, fromDateTime, toExclusive);
    }

    public long countByOwnerId(UUID ownerId) {
        return clickEventRepository.countByShortLink_Owner_Id(ownerId);
    }

    public long countByOwnerIdAndClickedAtBetween(UUID ownerId, LocalDateTime from, LocalDateTime to) {
        return clickEventRepository.countByShortLink_Owner_IdAndClickedAtGreaterThanEqualAndClickedAtLessThan(
                ownerId, from, to);
    }

    @Transactional
    public ClickEvent create(ClickEventCreateDto dto) {
        ShortLink shortLink = shortLinkService.findById(dto.getShortLinkId());
        boolean isUnique = Boolean.FALSE.equals(dto.getIsBot())
                && !clickEventRepository.existsByShortLink_IdAndIpHash(dto.getShortLinkId(), dto.getIpHash());

        ClickEvent saved = save(ClickEvent.builder()
                .shortLink(shortLink)
                .clickedAt(dto.getClickedAt())
                .ipHash(dto.getIpHash())
                .userAgent(dto.getUserAgent())
                .referrer(dto.getReferrer())
                .countryCode(dto.getCountryCode())
                .deviceType(dto.getDeviceType())
                .browser(dto.getBrowser())
                .operatingSystem(dto.getOperatingSystem())
                .isBot(dto.getIsBot())
                .build());

        if (Boolean.TRUE.equals(saved.getIsBot())) {
            return saved;
        }

        shortLinkService.incrementClickCounters(saved.getShortLink().getId(), isUnique);

        return saved;
    }
}
