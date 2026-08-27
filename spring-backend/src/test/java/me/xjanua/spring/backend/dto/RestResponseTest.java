package me.xjanua.spring.backend.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RestResponseTest {

    @Test
    void errorContainsCodeAndMessage() {
        RestResponse<Object> response = RestResponse.error("SHORT_LINK_NOT_FOUND", "Not found");

        assertThat(response.getSuccess()).isFalse();
        assertThat(response.getError()).isNotNull();
        assertThat(response.getError().getCode()).isEqualTo("SHORT_LINK_NOT_FOUND");
        assertThat(response.getError().getMessage()).isEqualTo("Not found");
        assertThat(response.getData()).isNull();
    }
}
