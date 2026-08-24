package me.xjanua.spring.backend.dto.redirect;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShortLinkUnlockRequest {

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(max = 128, message = "Mật khẩu không được dài quá 128 ký tự")
    private String password;
}
