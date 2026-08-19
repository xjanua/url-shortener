package me.xjanua.spring.backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.dto.PaginationDTO;
import me.xjanua.spring.backend.dto.shortLink.ShortLinkCreateDto;
import me.xjanua.spring.backend.dto.shortLink.ShortLinkResponseSummaryDto;
import me.xjanua.spring.backend.dto.shortLink.ShortLinkStatusUpdateDto;
import me.xjanua.spring.backend.dto.shortLink.ShortLinkStatsDto;
import me.xjanua.spring.backend.dto.shortLink.ShortLinkUpdateDto;
import me.xjanua.spring.backend.mapper.ShortLinkMapper;
import me.xjanua.spring.backend.model.ShortLink;
import me.xjanua.spring.backend.service.ShortLinkService;
import me.xjanua.spring.backend.service.ShortLinkStatsService;

@RestController
@RequestMapping("/short-links")
@RequiredArgsConstructor
public class ShortLinkController {

    private final ShortLinkService shortLinkService;
    private final ShortLinkStatsService shortLinkStatsService;
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

    @PutMapping("/{id}")
    public ResponseEntity<ShortLinkResponseSummaryDto> updateShortLink(@PathVariable Long id,
            @Valid @RequestBody ShortLinkUpdateDto request) {
        ShortLink shortLink = shortLinkService.updateShortLink(id, request);
        ShortLinkResponseSummaryDto response = shortLinkMapper.toSummaryResponse(shortLink, shortLinkBaseUrl);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ShortLinkResponseSummaryDto> updateShortLinkStatus(@PathVariable Long id,
            @Valid @RequestBody ShortLinkStatusUpdateDto request) {
        ShortLink shortLink = shortLinkService.updateStatus(id, request);
        ShortLinkResponseSummaryDto response = shortLinkMapper.toSummaryResponse(shortLink, shortLinkBaseUrl);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<ShortLinkStatsDto> getShortLinkStats(@PathVariable Long id) {
        ShortLinkStatsDto response = shortLinkStatsService.getStats(id);
        return ResponseEntity.ok(response);
    }

}
