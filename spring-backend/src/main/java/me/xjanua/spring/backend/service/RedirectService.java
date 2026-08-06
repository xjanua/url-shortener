package me.xjanua.spring.backend.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.dto.clickEvent.ClickEventCreateDto;

@Service
@RequiredArgsConstructor
public class RedirectService {

    private final ClickEventService clickEventService;
    private final IpInfoService ipInfoService;

    @Async
    @Transactional
    public void recordClickAsync(ClickEventCreateDto dto) {
        String countryCode = ipInfoService.getCountryCode(dto.getClientIp());
        dto.setCountryCode(countryCode);
        clickEventService.create(dto);
    }
}