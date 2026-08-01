package me.xjanua.spring.backend.dto.rolePermission;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RolePermissionRequest {
    private Long roleId;
    private Long permissionId;
}
