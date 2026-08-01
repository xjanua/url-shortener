package me.xjanua.spring.backend.service;

import me.xjanua.spring.backend.dto.UserDetailsCustom;
import me.xjanua.spring.backend.dto.auth.AuthenticationRequest;

public interface AuthService {
    UserDetailsCustom authenticate(AuthenticationRequest request);
}
