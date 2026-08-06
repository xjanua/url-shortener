package me.xjanua.spring.backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.dto.PaginationDTO;
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

    @GetMapping
    public ResponseEntity<PaginationDTO.Response> getAll(@Filter Specification<ShortLink> spec,
            Pageable pageable) {
        PaginationDTO.Response result = shortLinkService.fetchAll(spec, pageable);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<ShortLinkResponseSummaryDto> createShortLink(@Valid @RequestBody ShortLinkCreateDto request) {
        ShortLink shortLink = shortLinkService.createShortLink(request);
        ShortLinkResponseSummaryDto response = shortLinkMapper.toSummaryResponse(shortLink, shortLinkBaseUrl);
        return ResponseEntity.ok(response);
    }

}
