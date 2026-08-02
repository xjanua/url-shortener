package me.xjanua.spring.backend.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.dto.clickEvent.ClickEventCreateDto;
import me.xjanua.spring.backend.model.ShortLink;

@Service
@RequiredArgsConstructor
public class RedirectService {

    private final ShortLinkService shortLinkService;
    private final ClickEventService clickEventService;

    public ShortLink findShortLink(String shortCode) {
        return shortLinkService.findByShortCode(shortCode);
    }

    @Async
    @Transactional
    public void recordClickAsync(ClickEventCreateDto dto) {
        clickEventService.create(dto);
    }
}