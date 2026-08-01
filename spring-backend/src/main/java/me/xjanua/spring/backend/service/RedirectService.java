package me.xjanua.spring.backend.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.enums.ShortLinkStatus;
import me.xjanua.spring.backend.exception.NotFoundException;
import me.xjanua.spring.backend.model.ShortLink;
import me.xjanua.spring.backend.repository.ShortLinkRepository;

@Service
@RequiredArgsConstructor
public class RedirectService {

    private final ShortLinkRepository shortLinkRepository;

    public ShortLink resolveActiveLink(String shortCode) {
        ShortLink link = shortLinkRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new NotFoundException("Short link not found"));

        if (link.getStatus() != ShortLinkStatus.ACTIVE) {
            throw new NotFoundException("Short link is not available");
        }

        return link;
    }
}