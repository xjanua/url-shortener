package me.xjanua.spring.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.dto.PaginationDTO;
import me.xjanua.spring.backend.dto.permission.PermissionRequest;
import me.xjanua.spring.backend.dto.permission.PermissionResponse;
import me.xjanua.spring.backend.exception.NotFoundException;
import me.xjanua.spring.backend.mapper.PermissionMapper;
import me.xjanua.spring.backend.model.Permission;
import me.xjanua.spring.backend.repository.PermissionRepository;
import me.xjanua.spring.backend.util.PaginationUtil;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    public Permission findById(Long id) {
        return permissionRepository.findById(id).orElseThrow(() -> new NotFoundException("Permission not found"));
    }


    public Permission save(Permission permission) {
        return permissionRepository.save(permission);
    }

    public PaginationDTO.Response fetchAll(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> permissions = permissionRepository.findAll(spec, pageable);

        PaginationDTO.Info info = PaginationUtil.buildInfo(permissions, pageable);

        List<PermissionResponse> responses = permissions.getContent().stream()
                .map(permissionMapper::toResponse)
                .collect(Collectors.toList());

        PaginationDTO.Response response = new PaginationDTO.Response();
        response.setInfo(info);
        response.setResponse(responses);

        return response;
    }

    public Permission create(PermissionRequest request) {
        Permission permission = Permission.builder()
                .code(request.getCode())
                .description(request.getDescription())
                .build();
        return save(permission);
    }

    public Permission update(Long id, PermissionRequest request) {
        Permission permission = findById(id);
        permission.setCode(request.getCode());
        permission.setDescription(request.getDescription());
        return save(permission);
    }

    public void delete(Long id) {
        Permission permission = findById(id);
        permissionRepository.delete(permission);
    }
}

