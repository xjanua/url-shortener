package me.xjanua.spring.backend.controller;

import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.dto.clickEvent.ClickEventCreateDto;
import me.xjanua.spring.backend.dto.redirect.ShortLinkUnlockRequest;
import me.xjanua.spring.backend.enums.ShortLinkStatus;
import me.xjanua.spring.backend.exception.NotFoundException;
import me.xjanua.spring.backend.model.ShortLink;
import me.xjanua.spring.backend.service.RedirectService;
import me.xjanua.spring.backend.service.ShortLinkService;
import me.xjanua.spring.backend.util.ClickEventExtractor;
import me.xjanua.spring.backend.util.CommonUtils;

@Controller
@RequestMapping("/r")
@RequiredArgsConstructor
public class RedirectController {

    private final RedirectService redirectService;
    private final ShortLinkService shortLinkService;

    @GetMapping("/{shortCode}")
    public ModelAndView redirect(@PathVariable("shortCode") String shortCode, HttpServletRequest request) {
        ShortLink link = shortLinkService.findByShortCode(shortCode);

        if (!isAvailable(link)) {
            return goneView(link);
        }

        if (link.getPassword() != null) {
            return unlockView(link, new ShortLinkUnlockRequest());
        }

        recordClick(request, link);
        return redirectView(resolveDestinationUrl(link, request));
    }

    @PostMapping("/{shortCode}/unlock")
    public ModelAndView unlock(@PathVariable("shortCode") String shortCode,
            @Valid @ModelAttribute("unlockRequest") ShortLinkUnlockRequest request,
            BindingResult bindingResult,
            HttpServletRequest httpRequest) {
        ShortLink link = shortLinkService.findByShortCode(shortCode);

        if (!isAvailable(link)) {
            return goneView(link);
        }

        if (bindingResult.hasErrors()) {
            return unlockView(link, request);
        }

        if (!shortLinkService.matchesPassword(link, request.getPassword())) {
            bindingResult.rejectValue("password", "invalid", "Mật khẩu không đúng");
            return unlockView(link, request);
        }

        recordClick(httpRequest, link);
        return redirectView(resolveDestinationUrl(link, httpRequest));
    }

    @ExceptionHandler(NotFoundException.class)
    public ModelAndView handleNotFound(NotFoundException exception) {
        ModelAndView view = new ModelAndView("redirect/not-found");
        view.setStatus(HttpStatus.NOT_FOUND);
        return view;
    }

    private boolean isAvailable(ShortLink link) {
        ShortLinkStatus status = link.getStatus();
        return (status == ShortLinkStatus.ACTIVE || status == ShortLinkStatus.ARCHIVED)
                && !CommonUtils.isExpired(link.getExpiresAt());
    }

    private ModelAndView unlockView(ShortLink link, ShortLinkUnlockRequest request) {
        ModelAndView view = new ModelAndView("redirect/unlock");
        view.addObject("shortCode", link.getShortCode());
        view.addObject("linkTitle", link.getTitle());

        if (!view.getModel().containsKey("unlockRequest")) {
            view.addObject("unlockRequest", request);
        }

        return view;
    }

    private ModelAndView goneView(ShortLink link) {
        ModelAndView view = new ModelAndView("redirect/gone");
        view.addObject("linkTitle", link.getTitle());
        view.setStatus(HttpStatus.GONE);
        return view;
    }

    private ModelAndView redirectView(String destinationUrl) {
        RedirectView redirectView = new RedirectView(destinationUrl);
        redirectView.setStatusCode(HttpStatus.FOUND);
        ModelAndView view = new ModelAndView(redirectView);
        view.setStatus(HttpStatus.FOUND);
        return view;
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
