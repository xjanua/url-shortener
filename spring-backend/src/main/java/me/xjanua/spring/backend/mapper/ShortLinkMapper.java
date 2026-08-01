package me.xjanua.spring.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import me.xjanua.spring.backend.dto.shortLink.ShortLinkResponseSummaryDto;
import me.xjanua.spring.backend.model.ShortLink;

@Mapper(componentModel = "spring")
public interface ShortLinkMapper {

    @Mapping(target = "shortUrl", expression = "java(toShortUrl(shortLink.getShortCode(), baseUrl))")
    ShortLinkResponseSummaryDto toSummaryResponse(ShortLink shortLink, @org.mapstruct.Context String baseUrl);

    default String toShortUrl(String shortCode, String baseUrl) {
        return baseUrl + "/" + shortCode;
    }
}