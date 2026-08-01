package me.xjanua.spring.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestResponse<T> {

    private Boolean success;
    private RestResponseError error;
    private T data;

    public static <T> RestResponse<T> success(T data) {
        return new RestResponse<>(true, null, data);
    }

    public static <T> RestResponse<T> error(String message) {
        return new RestResponse<>(false, new RestResponseError(message), null);
    }
}
