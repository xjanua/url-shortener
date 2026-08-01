package me.xjanua.spring.backend.dto.shortLink;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShortLinkCreateDto {
    @NotBlank
    private String originalUrl;

    private String title;
}