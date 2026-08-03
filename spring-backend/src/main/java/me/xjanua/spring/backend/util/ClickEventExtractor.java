package me.xjanua.spring.backend.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HexFormat;

import jakarta.servlet.http.HttpServletRequest;
import me.xjanua.spring.backend.dto.clickEvent.ClickEventCreateDto;
import me.xjanua.spring.backend.model.ShortLink;

public final class ClickEventExtractor {

    private ClickEventExtractor() {
    }

    public static ClickEventCreateDto extract(HttpServletRequest request, ShortLink shortLink) {

        String userAgent = request.getHeader("User-Agent");
        String ip = resolveClientIp(request);

        System.out.println("Client IP = " + ip);
        String referrer = request.getHeader("Referer");

        return ClickEventCreateDto.builder()
                .shortLinkId(shortLink.getId())
                .clickedAt(LocalDateTime.now())
                .ipHash(hashIp(ip))
                .userAgent(userAgent)
                .referrer(referrer)
                .deviceType(parseDeviceType(userAgent))
                .browser(parseBrowser(userAgent))
                .operatingSystem(parseOs(userAgent))
                .isBot(isBot(userAgent))
                .build();
    }

    private static String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private static String hashIp(String ip) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(ip.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }

    private static String parseDeviceType(String ua) {
        if (ua == null)
            return "UNKNOWN";
        String lower = ua.toLowerCase();
        if (lower.contains("mobile"))
            return "MOBILE";
        if (lower.contains("tablet"))
            return "TABLET";
        return "DESKTOP";
    }

    private static String parseBrowser(String ua) {
        if (ua == null)
            return "UNKNOWN";
        if (ua.contains("Edg/"))
            return "EDGE";
        if (ua.contains("Chrome/"))
            return "CHROME";
        if (ua.contains("Firefox/"))
            return "FIREFOX";
        if (ua.contains("Safari/"))
            return "SAFA-RI".replace("-", "");
        return "OTHER";
    }

    private static String parseOs(String ua) {
        if (ua == null)
            return "UNKNOWN";
        if (ua.contains("Windows"))
            return "WINDOWS";
        if (ua.contains("Mac OS"))
            return "MACOS";
        if (ua.contains("Android"))
            return "ANDROID";
        if (ua.contains("iPhone") || ua.contains("iPad"))
            return "IOS";
        if (ua.contains("Linux"))
            return "LINUX";
        return "OTHER";
    }

    private static Boolean isBot(String ua) {
        if (ua == null)
            return false;
        String lower = ua.toLowerCase();
        return lower.contains("bot") ||
                lower.contains("crawler") ||
                lower.contains("spider") ||
                lower.contains("curl") ||
                lower.contains("wget");
    }
}