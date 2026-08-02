package me.xjanua.spring.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.dto.shortLink.ShortLinkCreateDto;
import me.xjanua.spring.backend.enums.ShortLinkStatus;
import me.xjanua.spring.backend.exception.NotFoundException;
import me.xjanua.spring.backend.model.ShortLink;
import me.xjanua.spring.backend.model.User;
import me.xjanua.spring.backend.repository.ShortLinkRepository;
import me.xjanua.spring.backend.util.ShortCodeGenerator;

@Service
@RequiredArgsConstructor
public class ShortLinkService {

    private final ShortLinkRepository shortLinkRepository;
    private final UserService userService;

    public ShortLink save(ShortLink shortLink) {
        return shortLinkRepository.save(shortLink);
    }

    public ShortLink findByShortCode(String shortCode) {
        return shortLinkRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new NotFoundException("Short link not found"));
    }

    public ShortLink findById(Long id) {
        return shortLinkRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Short link not found"));
    }

    public ShortLink createShortLink(ShortLinkCreateDto request) {
        String shortCode = ShortCodeGenerator.generate();
        User user = userService.fetchCurrentUser();

        ShortLink shortLink = ShortLink.builder()
            .owner(user)
            .originalUrl(request.getOriginalUrl())
            .title(request.getTitle())
            .shortCode(shortCode)
            .status(ShortLinkStatus.ACTIVE)
            .build();

        shortLink = save(shortLink);

        return shortLink;
    }

    @Transactional
    public void incrementClickCounters(Long shortLinkId, boolean isUnique) {
        ShortLink link = findById(shortLinkId);

        link.setClickCount(link.getClickCount() + 1);
        if (isUnique) {
            link.setUniqueClicks(link.getUniqueClicks() + 1);
        }
        save(link);
    }
}