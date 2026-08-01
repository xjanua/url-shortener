package me.xjanua.spring.backend.dto.rolePermission;


import lombok.Getter;
import lombok.Setter;
import me.xjanua.spring.backend.dto.permission.PermissionResponse;
import me.xjanua.spring.backend.dto.role.RoleResponse;

@Getter
@Setter
public class RolePermissionResponse {
    private Long id;
    private RoleResponse role;
    private PermissionResponse permission;
}
