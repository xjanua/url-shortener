package me.xjanua.spring.backend.mapper;

import org.mapstruct.Mapper;
import me.xjanua.spring.backend.dto.role.RoleResponse;
import me.xjanua.spring.backend.dto.role.RoleSummaryResponse;
import me.xjanua.spring.backend.model.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleResponse toResponse(Role role);

    RoleSummaryResponse toSummaryResponse(Role role);

}
