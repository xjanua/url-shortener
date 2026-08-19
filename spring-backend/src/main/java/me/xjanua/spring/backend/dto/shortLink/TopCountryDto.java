package me.xjanua.spring.backend.dto.shortLink;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TopCountryDto {

    private String code;
    private Long clicks;
}
