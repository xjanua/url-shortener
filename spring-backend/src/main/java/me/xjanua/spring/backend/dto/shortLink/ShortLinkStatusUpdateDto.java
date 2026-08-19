package me.xjanua.spring.backend.dto.shortLink;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import me.xjanua.spring.backend.enums.ShortLinkStatus;

@Getter
@Setter
public class ShortLinkStatusUpdateDto {

    @NotNull(message = "status không được để trống")
    private ShortLinkStatus status;
}
