package me.xjanua.spring.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.dto.PaginationDTO;
import me.xjanua.spring.backend.dto.rolePermission.RolePermissionRequest;
import me.xjanua.spring.backend.dto.rolePermission.RolePermissionResponse;
import me.xjanua.spring.backend.exception.BadRequestException;
import me.xjanua.spring.backend.exception.ErrorCode;
import me.xjanua.spring.backend.exception.NotFoundException;
import me.xjanua.spring.backend.mapper.RolePermissionMapper;
import me.xjanua.spring.backend.model.Permission;
import me.xjanua.spring.backend.model.Role;
import me.xjanua.spring.backend.model.RolePermission;
import me.xjanua.spring.backend.repository.RolePermissionRepository;
import me.xjanua.spring.backend.util.PaginationUtil;

@Service
@RequiredArgsConstructor
public class RolePermissionService {

    private final RolePermissionRepository rolePermissionRepository;
    private final RoleService roleService;
    private final PermissionService permissionService;
    private final RolePermissionMapper rolePermissionMapper;

    public List<RolePermission> findAll() {
        return rolePermissionRepository.findAll();
    }

    public RolePermission findById(Long id) {
        return rolePermissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ROLE_PERMISSION_NOT_FOUND,
                        "Role permission not found"));
    }

    public void delete(Long id) {
        RolePermission rolePermission = findById(id);
        rolePermissionRepository.delete(rolePermission);
    }

    public RolePermission save(RolePermission rolePermission) {
        return rolePermissionRepository.save(rolePermission);
    }

    public PaginationDTO.Response fetchAll(Specification<RolePermission> spec, Pageable pageable) {
        Page<RolePermission> rolePermissions = rolePermissionRepository.findAll(spec, pageable);

        PaginationDTO.Info info = PaginationUtil.buildInfo(rolePermissions, pageable);

        List<RolePermissionResponse> responses = rolePermissions.getContent().stream()
                .map(rolePermissionMapper::toResponse)
                .collect(Collectors.toList());

        PaginationDTO.Response response = new PaginationDTO.Response();
        response.setInfo(info);
        response.setResponse(responses);

        return response;
    }

    public List<String> findPermissionCodesByRoles(List<Role> roles) {
        return rolePermissionRepository.findByRoleIn(roles)
                .stream()
                .map(rolePermission -> rolePermission.getPermission().getCode())
                .distinct()
                .toList();
    }

    public RolePermission create(RolePermissionRequest request) {
        Role role = roleService.findById(request.getRoleId());
        Permission permission = permissionService.findById(request.getPermissionId());

        rolePermissionRepository.findByRoleAndPermission(role, permission).ifPresent(rp -> {
            throw new BadRequestException(ErrorCode.ROLE_PERMISSION_ALREADY_EXISTS,
                    "Role permission already exists");
        });

        RolePermission rolePermission = new RolePermission();
        rolePermission.setRole(role);
        rolePermission.setPermission(permission);
        return save(rolePermission);
    }

    public RolePermission update(Long id, RolePermissionRequest request) {
        RolePermission rolePermission = findById(id);

        Role role = roleService.findById(request.getRoleId());
        Permission permission = permissionService.findById(request.getPermissionId());

        rolePermission.setRole(role);
        rolePermission.setPermission(permission);
        return save(rolePermission);
    }

}
