package me.xjanua.spring.backend.service;

import java.util.List;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.dto.PaginationDTO;
import me.xjanua.spring.backend.dto.shortLink.ShortLinkCreateDto;
import me.xjanua.spring.backend.dto.shortLink.ShortLinkResponse;
import me.xjanua.spring.backend.enums.ShortLinkStatus;
import me.xjanua.spring.backend.exception.NotFoundException;
import me.xjanua.spring.backend.mapper.ShortLinkMapper;
import me.xjanua.spring.backend.model.ShortLink;
import me.xjanua.spring.backend.model.User;
import me.xjanua.spring.backend.repository.ShortLinkRepository;
import me.xjanua.spring.backend.util.PaginationUtil;
import me.xjanua.spring.backend.util.ShortCodeGenerator;
import me.xjanua.spring.backend.util.SecurityUtil;

@Service
@RequiredArgsConstructor
public class ShortLinkService {

    private final ShortLinkRepository shortLinkRepository;
    private final UserService userService;
    private final ShortLinkMapper shortLinkMapper;

    @Value("${short-link.base-url}")
    private String shortLinkBaseUrl;

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

    public long countByOwnerId(UUID ownerId) {
        return shortLinkRepository.countByOwner_Id(ownerId);
    }

    public long countCreatedByOwnerId(UUID ownerId, LocalDateTime from, LocalDateTime to) {
        return shortLinkRepository.countByOwner_IdAndCreatedAtGreaterThanEqualAndCreatedAtLessThan(ownerId, from, to);
    }

    public PaginationDTO.Response fetchAll(Specification<ShortLink> spec, Pageable pageable) {
        UUID currentUserId = SecurityUtil.getCurrentUserId();

        Specification<ShortLink> ownerSpec = (root, query, cb) -> cb.equal(root.get("owner").get("id"), currentUserId);

        Specification<ShortLink> finalSpec = ownerSpec;

        if (spec != null) {
            finalSpec = ownerSpec.and(spec);
        }

        Page<ShortLink> shortLinks = shortLinkRepository.findAll(finalSpec, pageable);
        PaginationDTO.Info info = PaginationUtil.buildInfo(shortLinks, pageable);
        List<ShortLinkResponse> responses = shortLinks.getContent()
                .stream()
                .map(shortLink -> shortLinkMapper.toResponse(shortLink, shortLinkBaseUrl))
                .toList();
        PaginationDTO.Response response = new PaginationDTO.Response();
        response.setInfo(info);
        response.setResponse(responses);

        return response;
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
