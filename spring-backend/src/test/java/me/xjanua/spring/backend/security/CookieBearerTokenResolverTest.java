package me.xjanua.spring.backend.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import jakarta.servlet.http.Cookie;

class CookieBearerTokenResolverTest {

    private final CookieBearerTokenResolver resolver =
            new CookieBearerTokenResolver("url_shortener_access");

    @Test
    void resolvesBearerHeaderBeforeCookie() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer header-token");
        request.setCookies(new Cookie("url_shortener_access", "cookie-token"));

        assertThat(resolver.resolve(request)).isEqualTo("header-token");
    }

    @Test
    void resolvesAccessTokenFromCookieWhenHeaderIsMissing() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("url_shortener_access", "cookie-token"));

        assertThat(resolver.resolve(request)).isEqualTo("cookie-token");
    }

    @Test
    void returnsNullWhenAuthenticationIsMissing() {
        assertThat(resolver.resolve(new MockHttpServletRequest())).isNull();
    }
}
