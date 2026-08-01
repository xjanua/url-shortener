package me.xjanua.spring.backend.mapper;


import org.mapstruct.Mapper;

import me.xjanua.spring.backend.dto.permission.PermissionResponse;
import me.xjanua.spring.backend.model.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    PermissionResponse toResponse(Permission permission);
}
