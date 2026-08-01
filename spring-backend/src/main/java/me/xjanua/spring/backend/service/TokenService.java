package me.xjanua.spring.backend.service;

import java.util.Collection;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;

public interface TokenService {
    String createAccessToken(UUID userId, Collection<? extends GrantedAuthority> authorities);
}
