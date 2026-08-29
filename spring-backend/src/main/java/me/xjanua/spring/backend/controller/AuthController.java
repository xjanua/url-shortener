package me.xjanua.spring.backend.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.dto.UserDetailsCustom;
import me.xjanua.spring.backend.dto.auth.AuthenticationRequest;
import me.xjanua.spring.backend.dto.auth.ResponseLoginDto;
import me.xjanua.spring.backend.dto.user.UserRegisterRequest;
import me.xjanua.spring.backend.security.AuthCookieService;
import me.xjanua.spring.backend.service.AuthService;
import me.xjanua.spring.backend.service.TokenService;
import me.xjanua.spring.backend.service.UserService;

@RequestMapping("/auth")
@RequiredArgsConstructor
@RestController
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;
    private final UserService userService;
    private final AuthCookieService authCookieService;
    
    @PostMapping("/login")
    public ResponseEntity<ResponseLoginDto> login(@RequestBody AuthenticationRequest request) {
        UserDetailsCustom userDetails = authService.authenticate(request);

        ResponseLoginDto res = new ResponseLoginDto();
        String accessToken = tokenService.createAccessToken(userDetails.getUserId(), userDetails.getAuthorities());
        res.setAccessToken(accessToken);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authCookieService.createAccessTokenCookie(accessToken).toString())
                .body(res);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody UserRegisterRequest request) {
        userService.register(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authCookieService.clearAccessTokenCookie().toString())
                .build();
    }
}
