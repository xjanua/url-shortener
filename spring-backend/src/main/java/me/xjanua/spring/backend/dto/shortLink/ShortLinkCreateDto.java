package me.xjanua.spring.backend.dto.shortLink;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShortLinkCreateDto {
    @NotBlank
    private String originalUrl;

    @Size(max = 2048, message = "androidUrl không được dài quá 2048 ký tự")
    private String androidUrl;

    @Size(max = 2048, message = "iosUrl không được dài quá 2048 ký tự")
    private String iosUrl;

    @Size(max = 2048, message = "desktopUrl không được dài quá 2048 ký tự")
    private String desktopUrl;

    private String title;

    @Size(max = 32, message = "shortCode không được dài quá 32 ký tự")
    @Pattern(regexp = "^[A-Za-z0-9_-]*$", message = "shortCode chỉ được chứa chữ, số, dấu gạch ngang và gạch dưới")
    private String shortCode;

    private LocalDateTime expiresAt;

    private String password;
}
