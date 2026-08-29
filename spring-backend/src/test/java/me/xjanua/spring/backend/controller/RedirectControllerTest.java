package me.xjanua.spring.backend.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import me.xjanua.spring.backend.dto.clickEvent.ClickEventCreateDto;
import me.xjanua.spring.backend.dto.redirect.ShortLinkUnlockRequest;
import me.xjanua.spring.backend.enums.ShortLinkStatus;
import me.xjanua.spring.backend.exception.ErrorCode;
import me.xjanua.spring.backend.exception.NotFoundException;
import me.xjanua.spring.backend.model.ShortLink;
import me.xjanua.spring.backend.service.RedirectService;
import me.xjanua.spring.backend.service.ShortLinkService;

class RedirectControllerTest {

    private RedirectService redirectService;
    private ShortLinkService shortLinkService;
    private RedirectController controller;
    private MockHttpServletRequest httpRequest;

    @BeforeEach
    void setUp() {
        redirectService = mock(RedirectService.class);
        shortLinkService = mock(ShortLinkService.class);
        controller = new RedirectController(redirectService, shortLinkService);
        httpRequest = new MockHttpServletRequest();
        httpRequest.setRemoteAddr("127.0.0.1");
        httpRequest.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0) Chrome/140.0");
    }

    @Test
    void activePublicLinkRecordsClickAndRedirects() {
        ShortLink link = activeLink();
        when(shortLinkService.findByShortCode("demo")).thenReturn(link);

        ModelAndView result = controller.redirect("demo", httpRequest);

        assertThat(result.getView()).isInstanceOf(RedirectView.class);
        RedirectView redirectView = (RedirectView) result.getView();
        assertThat(redirectView.getUrl()).isEqualTo("https://example.com");
        assertThat(result.getStatus()).isEqualTo(HttpStatus.FOUND);
        verify(redirectService).recordClickAsync(any(ClickEventCreateDto.class));
    }

    @Test
    void protectedLinkRendersUnlockFormWithoutRecordingClick() {
        ShortLink link = activeLink();
        link.setPassword("encoded-password");
        when(shortLinkService.findByShortCode("demo")).thenReturn(link);

        ModelAndView result = controller.redirect("demo", httpRequest);

        assertThat(result.getViewName()).isEqualTo("redirect/unlock");
        assertThat(result.getModel()).containsEntry("shortCode", "demo");
        assertThat(result.getModel().get("unlockRequest")).isInstanceOf(ShortLinkUnlockRequest.class);
        verify(redirectService, never()).recordClickAsync(any());
    }

    @Test
    void expiredLinkRendersGonePageWith410() {
        ShortLink link = activeLink();
        link.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        when(shortLinkService.findByShortCode("demo")).thenReturn(link);

        ModelAndView result = controller.redirect("demo", httpRequest);

        assertThat(result.getViewName()).isEqualTo("redirect/gone");
        assertThat(result.getStatus()).isEqualTo(HttpStatus.GONE);
        verify(redirectService, never()).recordClickAsync(any());
    }

    @Test
    void missingLinkRendersNotFoundPageWith404() {
        ModelAndView result = controller.handleNotFound(
                new NotFoundException(ErrorCode.SHORT_LINK_NOT_FOUND, "Short link not found"));

        assertThat(result.getViewName()).isEqualTo("redirect/not-found");
        assertThat(result.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void invalidPasswordRendersFormErrorWithoutRecordingClick() {
        ShortLink link = activeLink();
        link.setPassword("encoded-password");
        ShortLinkUnlockRequest request = new ShortLinkUnlockRequest();
        request.setPassword("wrong-password");
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(request, "unlockRequest");

        when(shortLinkService.findByShortCode("demo")).thenReturn(link);
        when(shortLinkService.matchesPassword(link, request.getPassword())).thenReturn(false);

        ModelAndView result = controller.unlock("demo", request, bindingResult, httpRequest);

        assertThat(result.getViewName()).isEqualTo("redirect/unlock");
        assertThat(bindingResult.getFieldError("password")).isNotNull();
        assertThat(bindingResult.getFieldError("password").getDefaultMessage()).isEqualTo("Mật khẩu không đúng");
        verify(redirectService, never()).recordClickAsync(any());
    }

    @Test
    void validPasswordRecordsClickAndRedirects() {
        ShortLink link = activeLink();
        link.setPassword("encoded-password");
        ShortLinkUnlockRequest request = new ShortLinkUnlockRequest();
        request.setPassword("correct-password");
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(request, "unlockRequest");

        when(shortLinkService.findByShortCode("demo")).thenReturn(link);
        when(shortLinkService.matchesPassword(link, request.getPassword())).thenReturn(true);

        ModelAndView result = controller.unlock("demo", request, bindingResult, httpRequest);

        assertThat(result.getView()).isInstanceOf(RedirectView.class);
        assertThat(((RedirectView) result.getView()).getUrl()).isEqualTo("https://example.com");
        verify(redirectService).recordClickAsync(any(ClickEventCreateDto.class));
    }

    private ShortLink activeLink() {
        return ShortLink.builder()
                .id(1L)
                .shortCode("demo")
                .title("Demo link")
                .originalUrl("https://example.com")
                .status(ShortLinkStatus.ACTIVE)
                .build();
    }
}
