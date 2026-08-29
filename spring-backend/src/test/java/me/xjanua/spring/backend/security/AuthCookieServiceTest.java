package me.xjanua.spring.backend.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;

class AuthCookieServiceTest {

    private final AuthCookieService cookieService =
            new AuthCookieService("url_shortener_access", true, "Lax", 900);

    @Test
    void createsSecureHttpOnlyAccessTokenCookie() {
        ResponseCookie cookie = cookieService.createAccessTokenCookie("token");

        assertThat(cookie.getName()).isEqualTo("url_shortener_access");
        assertThat(cookie.getValue()).isEqualTo("token");
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.isSecure()).isTrue();
        assertThat(cookie.getSameSite()).isEqualTo("Lax");
        assertThat(cookie.getPath()).isEqualTo("/");
        assertThat(cookie.getMaxAge().getSeconds()).isEqualTo(900);
    }

    @Test
    void clearsAccessTokenCookieImmediately() {
        ResponseCookie cookie = cookieService.clearAccessTokenCookie();

        assertThat(cookie.getValue()).isEmpty();
        assertThat(cookie.getMaxAge().isZero()).isTrue();
    }
}
