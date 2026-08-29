package me.xjanua.spring.backend.security;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class AuthCookieService {

    private final String cookieName;
    private final boolean secure;
    private final String sameSite;
    private final Duration maxAge;

    public AuthCookieService(
            @Value("${auth.cookie.name:url_shortener_access}") String cookieName,
            @Value("${auth.cookie.secure:false}") boolean secure,
            @Value("${auth.cookie.same-site:Lax}") String sameSite,
            @Value("${jwt.access-token-validity-in-seconds}") long maxAgeSeconds) {
        this.cookieName = cookieName;
        this.secure = secure;
        this.sameSite = sameSite;
        this.maxAge = Duration.ofSeconds(maxAgeSeconds);
    }

    public ResponseCookie createAccessTokenCookie(String accessToken) {
        return baseCookie(accessToken)
                .maxAge(maxAge)
                .build();
    }

    public ResponseCookie clearAccessTokenCookie() {
        return baseCookie("")
                .maxAge(Duration.ZERO)
                .build();
    }

    private ResponseCookie.ResponseCookieBuilder baseCookie(String value) {
        return ResponseCookie.from(cookieName, value)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path("/");
    }
}
