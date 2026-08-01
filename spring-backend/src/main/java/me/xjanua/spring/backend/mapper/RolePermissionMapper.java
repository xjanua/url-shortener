package me.xjanua.spring.backend.mapper;


import org.mapstruct.Mapper;

import me.xjanua.spring.backend.dto.rolePermission.RolePermissionResponse;
import me.xjanua.spring.backend.model.RolePermission;

@Mapper(componentModel = "spring")
public interface RolePermissionMapper {

    RolePermissionResponse toResponse(RolePermission rolePermission);
}
