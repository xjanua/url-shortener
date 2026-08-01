package me.xjanua.spring.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import me.xjanua.spring.backend.dto.user.UserDetailResponse;
import me.xjanua.spring.backend.model.User;

@Mapper(componentModel = "spring", uses = RoleMapper.class)
public interface UserMapper {

    @Mapping(target = "roles", ignore = true)
    UserDetailResponse toDetailResponse(User user);
}
