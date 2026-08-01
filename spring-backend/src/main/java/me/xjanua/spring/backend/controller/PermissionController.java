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
import me.xjanua.spring.backend.dto.permission.PermissionRequest;
import me.xjanua.spring.backend.dto.permission.PermissionResponse;
import me.xjanua.spring.backend.mapper.PermissionMapper;
import me.xjanua.spring.backend.model.Permission;
import me.xjanua.spring.backend.service.PermissionService;

@RequestMapping("/permissions")
@RequiredArgsConstructor
@RestController
public class PermissionController {

    private final PermissionService permissionService;
    private final PermissionMapper permissionMapper;

    @GetMapping
    public ResponseEntity<PaginationDTO.Response> getAll(@Filter Specification<Permission> spec, Pageable pageable) {
        PaginationDTO.Response result = permissionService.fetchAll(spec, pageable);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<PermissionResponse> create(@RequestBody PermissionRequest request) {
        Permission created = permissionService.create(request);
        PermissionResponse response = permissionMapper.toResponse(created);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PermissionResponse> update(@PathVariable Long id, @RequestBody PermissionRequest request) {
        Permission updated = permissionService.update(id, request);
        PermissionResponse response = permissionMapper.toResponse(updated);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        permissionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

