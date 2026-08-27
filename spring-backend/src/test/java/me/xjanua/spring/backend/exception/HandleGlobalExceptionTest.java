package me.xjanua.spring.backend.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import me.xjanua.spring.backend.dto.RestResponseError;

class HandleGlobalExceptionTest {

    private final HandleGlobalException handler = new HandleGlobalException();

    @Test
    void badRequestPreservesBusinessErrorCode() {
        BadRequestException exception = new BadRequestException(
                ErrorCode.SHORT_CODE_ALREADY_EXISTS,
                "shortCode đã được sử dụng");

        ResponseEntity<RestResponseError> response = handler.handleBadRequest(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCode()).isEqualTo(ErrorCode.SHORT_CODE_ALREADY_EXISTS);
        assertThat(response.getBody().getMessage()).isEqualTo("shortCode đã được sử dụng");
    }
}
