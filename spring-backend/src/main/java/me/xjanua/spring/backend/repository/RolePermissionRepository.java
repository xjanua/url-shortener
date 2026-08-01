package me.xjanua.spring.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import me.xjanua.spring.backend.model.Permission;
import me.xjanua.spring.backend.model.Role;
import me.xjanua.spring.backend.model.RolePermission;

public interface RolePermissionRepository
        extends JpaRepository<RolePermission, Long>, JpaSpecificationExecutor<RolePermission> {

    List<RolePermission> findByRoleIn(List<Role> roles);

    List<RolePermission> findByRole(Role role);

    Optional<RolePermission> findByRoleAndPermission(Role role, Permission permission);
}
