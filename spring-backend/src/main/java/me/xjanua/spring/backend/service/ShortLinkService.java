package me.xjanua.spring.backend.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.dto.shortLink.ShortLinkCreateDto;
import me.xjanua.spring.backend.enums.ShortLinkStatus;
import me.xjanua.spring.backend.model.ShortLink;
import me.xjanua.spring.backend.repository.ShortLinkRepository;
import me.xjanua.spring.backend.util.ShortCodeGenerator;

@Service
@RequiredArgsConstructor
public class ShortLinkService {

    private final ShortLinkRepository shortLinkRepository;

    public ShortLink save(ShortLink shortLink) {
        return shortLinkRepository.save(shortLink);
    }
    
    public ShortLink createShortLink(ShortLinkCreateDto request) {
        String shortCode = ShortCodeGenerator.generate();

        ShortLink shortLink = ShortLink.builder()
            .originalUrl(request.getOriginalUrl())
            .title(request.getTitle())
            .shortCode(shortCode)
            .status(ShortLinkStatus.ACTIVE)
            .build();

        shortLink = save(shortLink);
            
        return shortLink;
    }
}