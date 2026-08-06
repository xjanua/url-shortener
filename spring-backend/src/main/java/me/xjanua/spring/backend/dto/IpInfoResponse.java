package me.xjanua.spring.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IpInfoResponse {

    private String ip;
    private String asn;

    @JsonProperty("as_name")
    private String asName;

    @JsonProperty("as_domain")
    private String asDomain;

    @JsonProperty("country_code")
    private String countryCode;

    private String country;

    @JsonProperty("continent_code")
    private String continentCode;

    private String continent;
}