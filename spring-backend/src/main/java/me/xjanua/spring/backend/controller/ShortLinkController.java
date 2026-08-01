package me.xjanua.spring.backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.dto.shortLink.ShortLinkCreateDto;
import me.xjanua.spring.backend.dto.shortLink.ShortLinkResponseSummaryDto;
import me.xjanua.spring.backend.mapper.ShortLinkMapper;
import me.xjanua.spring.backend.model.ShortLink;
import me.xjanua.spring.backend.service.ShortLinkService;

@RestController
@RequestMapping("/short-links")
@RequiredArgsConstructor
public class ShortLinkController {
    
    private final ShortLinkService shortLinkService;
    private final ShortLinkMapper shortLinkMapper;

    @Value("${short-link.base-url}")
    private String shortLinkBaseUrl;

    @PostMapping
    public ResponseEntity<ShortLinkResponseSummaryDto> createShortLink(@Valid @RequestBody ShortLinkCreateDto request) {
        ShortLink shortLink = shortLinkService.createShortLink(request);
        ShortLinkResponseSummaryDto response = shortLinkMapper.toSummaryResponse(shortLink, shortLinkBaseUrl);
        return ResponseEntity.ok(response);
    }

}