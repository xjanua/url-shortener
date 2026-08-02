package me.xjanua.spring.backend.dto.redirect;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedirectResponseDto {
    private String originalUrl;
    private String status;
}