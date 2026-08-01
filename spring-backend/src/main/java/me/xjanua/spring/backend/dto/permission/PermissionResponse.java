package me.xjanua.spring.backend.dto.permission;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionResponse {
    private Long id;
    private String code;
    private String description;
}

