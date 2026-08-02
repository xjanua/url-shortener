package me.xjanua.spring.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.dto.clickEvent.ClickEventCreateDto;
import me.xjanua.spring.backend.model.ClickEvent;
import me.xjanua.spring.backend.repository.ClickEventRepository;

@Service
@RequiredArgsConstructor
public class ClickEventService {

    private final ClickEventRepository clickEventRepository;
    private final ShortLinkService shortLinkService;

    public ClickEvent save(ClickEvent clickEvent) {
        return clickEventRepository.save(clickEvent);
    }

    @Transactional
    public ClickEvent create(ClickEventCreateDto dto) {
        boolean isUnique = Boolean.FALSE.equals(dto.getIsBot())
                && !clickEventRepository.existsByShortLink_IdAndIpHash(
                        dto.getShortLink().getId(), dto.getIpHash());

        ClickEvent saved = save(ClickEvent.builder()
                .shortLink(dto.getShortLink())
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