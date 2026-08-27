package me.xjanua.spring.backend.config;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import jakarta.servlet.http.HttpServletResponse;
import me.xjanua.spring.backend.dto.RestResponse;
import me.xjanua.spring.backend.dto.RestResponseError;
import me.xjanua.spring.backend.exception.ErrorCode;

@RestControllerAdvice
public class FormatRestResponse implements ResponseBodyAdvice {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {
        HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();

        int status = servletResponse.getStatus();
        String path = request.getURI().getPath();

        if (path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui")) {
            return body;
        }

        if (status >= 400) {
            if (body instanceof RestResponseError apiError) {
                return RestResponse.error(apiError.getCode(), apiError.getMessage());
            }
            return RestResponse.error(resolveErrorCode(status), body != null ? body.toString() : "Unknown error");
        }

        return RestResponse.success(body);
    }

    private String resolveErrorCode(int status) {
        return switch (status) {
            case HttpServletResponse.SC_BAD_REQUEST -> ErrorCode.BAD_REQUEST;
            case HttpServletResponse.SC_UNAUTHORIZED -> ErrorCode.UNAUTHORIZED;
            case HttpServletResponse.SC_FORBIDDEN -> ErrorCode.FORBIDDEN;
            case HttpServletResponse.SC_NOT_FOUND -> ErrorCode.RESOURCE_NOT_FOUND;
            default -> status >= HttpServletResponse.SC_INTERNAL_SERVER_ERROR
                    ? ErrorCode.INTERNAL_SERVER_ERROR
                    : ErrorCode.BAD_REQUEST;
        };
    }
}
