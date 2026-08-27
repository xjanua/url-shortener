package me.xjanua.spring.backend.controller;

import java.net.URI;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.dto.RestResponseError;
import me.xjanua.spring.backend.dto.clickEvent.ClickEventCreateDto;
import me.xjanua.spring.backend.dto.redirect.ShortLinkUnlockRequest;
import me.xjanua.spring.backend.enums.ShortLinkStatus;
import me.xjanua.spring.backend.exception.ErrorCode;
import me.xjanua.spring.backend.model.ShortLink;
import me.xjanua.spring.backend.service.RedirectService;
import me.xjanua.spring.backend.service.ShortLinkService;
import me.xjanua.spring.backend.util.ClickEventExtractor;
import me.xjanua.spring.backend.util.CommonUtils;

@RestController
@RequestMapping("/r")
@RequiredArgsConstructor
public class RedirectController {

    private final RedirectService redirectService;
    private final ShortLinkService shortLinkService;

    @GetMapping("/{shortCode}")
    public ResponseEntity<?> redirect(@PathVariable("shortCode") String shortCode, HttpServletRequest request) {

        ShortLink link = shortLinkService.findByShortCode(shortCode);
        ShortLinkStatus status = link.getStatus();

        if ((status != ShortLinkStatus.ACTIVE && status != ShortLinkStatus.ARCHIVED)
                || CommonUtils.isExpired(link.getExpiresAt())) {
            return ResponseEntity.status(HttpStatus.GONE)
                    .body(new RestResponseError(ErrorCode.SHORT_LINK_GONE, "Short link is inactive or expired"));
        }

        if (link.getPassword() != null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new RestResponseError(ErrorCode.SHORT_LINK_PASSWORD_REQUIRED,
                            "Short link requires a password"));
        }

        recordClick(request, link);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(resolveDestinationUrl(link, request)))
                .build();
    }

    @PostMapping("/{shortCode}/unlock")
    public ResponseEntity<?> unlock(@PathVariable("shortCode") String shortCode,
            @Valid @RequestBody ShortLinkUnlockRequest request,
            HttpServletRequest httpRequest) {
        ShortLink link = shortLinkService.findByShortCode(shortCode);
        ShortLinkStatus status = link.getStatus();

        if ((status != ShortLinkStatus.ACTIVE && status != ShortLinkStatus.ARCHIVED)
                || CommonUtils.isExpired(link.getExpiresAt())) {
            return ResponseEntity.status(HttpStatus.GONE)
                    .body(new RestResponseError(ErrorCode.SHORT_LINK_GONE, "Short link is inactive or expired"));
        }

        if (!shortLinkService.matchesPassword(link, request.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new RestResponseError(ErrorCode.INVALID_SHORT_LINK_PASSWORD, "Invalid password"));
        }

        recordClick(httpRequest, link);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(resolveDestinationUrl(link, httpRequest)))
                .build();
    }

    private void recordClick(HttpServletRequest request, ShortLink link) {
        ClickEventCreateDto eventDto = ClickEventExtractor.extract(request, link);
        redirectService.recordClickAsync(eventDto);
    }

    private String resolveDestinationUrl(ShortLink link, HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String normalizedUserAgent = userAgent == null ? "" : userAgent.toLowerCase(Locale.ROOT);

        if (normalizedUserAgent.contains("android") && link.getAndroidUrl() != null) {
            return link.getAndroidUrl();
        }

        if ((normalizedUserAgent.contains("iphone") || normalizedUserAgent.contains("ipad"))
                && link.getIosUrl() != null) {
            return link.getIosUrl();
        }

        if (!normalizedUserAgent.contains("mobile") && link.getDesktopUrl() != null) {
            return link.getDesktopUrl();
        }

        return link.getOriginalUrl();
    }
}
