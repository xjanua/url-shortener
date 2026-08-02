package me.xjanua.spring.backend.controller;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.dto.clickEvent.ClickEventCreateDto;
import me.xjanua.spring.backend.enums.ShortLinkStatus;
import me.xjanua.spring.backend.model.ShortLink;
import me.xjanua.spring.backend.service.RedirectService;
import me.xjanua.spring.backend.service.ShortLinkService;
import me.xjanua.spring.backend.util.ClickEventExtractor;

@RestController
@RequestMapping("/r")
@RequiredArgsConstructor
public class RedirectController {

    private final RedirectService redirectService;
    private final ShortLinkService shortLinkService;

    @GetMapping("/{shortCode}")
    public ResponseEntity<?> redirect(@PathVariable(name = "shortCode") String shortCode, HttpServletRequest request) {

        ShortLink link = shortLinkService.findByShortCode(shortCode);
        ShortLinkStatus status = link.getStatus();

        if (status == ShortLinkStatus.ACTIVE || status == ShortLinkStatus.ARCHIVED) {
            ClickEventCreateDto eventDto = ClickEventExtractor.extract(request, link);
            redirectService.recordClickAsync(eventDto);

            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(link.getOriginalUrl()))
                    .build();
        }

        return ResponseEntity.status(HttpStatus.GONE).build();
    }
}