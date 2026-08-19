package me.xjanua.spring.backend.service;

import java.util.List;
import java.util.Objects;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.dto.PaginationDTO;
import me.xjanua.spring.backend.dto.shortLink.ShortLinkCreateDto;
import me.xjanua.spring.backend.dto.shortLink.ShortLinkResponse;
import me.xjanua.spring.backend.dto.shortLink.ShortLinkStatusUpdateDto;
import me.xjanua.spring.backend.dto.shortLink.ShortLinkUpdateDto;
import me.xjanua.spring.backend.enums.ShortCodeType;
import me.xjanua.spring.backend.enums.ShortLinkStatus;
import me.xjanua.spring.backend.exception.BadRequestException;
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
    private final PasswordEncoder passwordEncoder;

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
        boolean hasCustomShortCode = StringUtils.hasText(request.getShortCode());
        boolean hasPassword = StringUtils.hasText(request.getPassword());
        String shortCode = hasCustomShortCode ? request.getShortCode().trim() : ShortCodeGenerator.generate();

        if (hasCustomShortCode && shortLinkRepository.existsByShortCode(shortCode)) {
            throw new BadRequestException("shortCode đã được sử dụng");
        }

        User user = userService.fetchCurrentUser();

        ShortLink shortLink = ShortLink.builder()
                .owner(user)
                .originalUrl(request.getOriginalUrl())
                .title(request.getTitle())
                .shortCode(shortCode)
                .shortCodeType(hasCustomShortCode ? ShortCodeType.CUSTOM : ShortCodeType.GENERATED)
                .password(hasPassword ? passwordEncoder.encode(request.getPassword()) : null)
                .status(ShortLinkStatus.ACTIVE)
                .expiresAt(request.getExpiresAt())
                .build();

        shortLink = save(shortLink);

        return shortLink;
    }

    public ShortLink updateShortLink(Long id, ShortLinkUpdateDto request) {
        ShortLink shortLink = findById(id);
        UUID currentUserId = SecurityUtil.getCurrentUserId();

        if (!isOwner(shortLink.getOwner().getId(), currentUserId)) {
            throw new AccessDeniedException("Bạn không có quyền cập nhật short link này");
        }

        if (StringUtils.hasText(request.getShortCode())) {
            String shortCode = request.getShortCode().trim();

            if (!shortCode.equals(shortLink.getShortCode()) && shortLinkRepository.existsByShortCode(shortCode)) {
                throw new BadRequestException("shortCode đã được sử dụng");
            }

            shortLink.setShortCode(shortCode);
            shortLink.setShortCodeType(ShortCodeType.CUSTOM);
        }

        shortLink.setOriginalUrl(request.getOriginalUrl());
        shortLink.setTitle(request.getTitle());
        shortLink.setExpiresAt(request.getExpiresAt());

        if (request.getPassword() != null) {
            if (StringUtils.hasText(request.getPassword())) {
                shortLink.setPassword(
                        passwordEncoder.encode(request.getPassword()));
            } else {
                shortLink.setPassword(null);
            }
        }

        return save(shortLink);
    }

    public ShortLink updateStatus(Long id, ShortLinkStatusUpdateDto request) {
        ShortLink shortLink = findById(id);
        UUID currentUserId = SecurityUtil.getCurrentUserId();

        if (!isOwner(shortLink.getOwner().getId(), currentUserId)) {
            throw new AccessDeniedException("Bạn không có quyền cập nhật trạng thái short link này");
        }

        if (shortLink.getStatus() == ShortLinkStatus.DELETED) {
            throw new BadRequestException("Short link đã bị xoá và không thể thay đổi trạng thái");
        }

        shortLink.setStatus(request.getStatus());
        return save(shortLink);
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

    public boolean isOwner(UUID userId, UUID ownerCurrent) {
        return Objects.equals(userId, ownerCurrent);
    }
}
