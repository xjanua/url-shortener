package me.xjanua.spring.backend.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.dto.PaginationDTO;
import me.xjanua.spring.backend.dto.rolePermission.RolePermissionRequest;
import me.xjanua.spring.backend.dto.rolePermission.RolePermissionResponse;
import me.xjanua.spring.backend.mapper.RolePermissionMapper;
import me.xjanua.spring.backend.model.RolePermission;
import me.xjanua.spring.backend.service.RolePermissionService;

@RequestMapping("/role-permissions")
@RequiredArgsConstructor
@RestController
public class RolePermissionController {

    private final RolePermissionService rolePermissionService;
    private final RolePermissionMapper rolePermissionMapper;

    @GetMapping
    public ResponseEntity<PaginationDTO.Response> getAll(@Filter Specification<RolePermission> spec,
            Pageable pageable) {
        PaginationDTO.Response result = rolePermissionService.fetchAll(spec, pageable);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<RolePermissionResponse> create(@RequestBody RolePermissionRequest request) {
        RolePermission created = rolePermissionService.create(request);
        RolePermissionResponse response = rolePermissionMapper.toResponse(created);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RolePermissionResponse> update(@PathVariable Long id,
            @RequestBody RolePermissionRequest request) {
        RolePermission updated = rolePermissionService.update(id, request);
        RolePermissionResponse response = rolePermissionMapper.toResponse(updated);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        rolePermissionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
