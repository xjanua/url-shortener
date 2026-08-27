package me.xjanua.spring.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.dto.PaginationDTO;
import me.xjanua.spring.backend.dto.role.RoleRequest;
import me.xjanua.spring.backend.dto.role.RoleResponse;
import me.xjanua.spring.backend.exception.NotFoundException;
import me.xjanua.spring.backend.exception.ErrorCode;
import me.xjanua.spring.backend.mapper.RoleMapper;
import me.xjanua.spring.backend.model.Role;
import me.xjanua.spring.backend.repository.RoleRepository;
import me.xjanua.spring.backend.util.PaginationUtil;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    public Role save(Role role) {
        return roleRepository.save(role);
    }

    public PaginationDTO.Response fetchAll(Specification<Role> spec, Pageable pageable) {
        Page<Role> roles = roleRepository.findAll(spec, pageable);

        PaginationDTO.Info info = PaginationUtil.buildInfo(roles, pageable);

        List<RoleResponse> responses = roles.getContent().stream()
                .map(roleMapper::toResponse)
                .collect(Collectors.toList());

        PaginationDTO.Response response = new PaginationDTO.Response();
        response.setInfo(info);
        response.setResponse(responses);

        return response;
    }

    public Role findById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ROLE_NOT_FOUND, "Role not found"));
    }

    public Role create(RoleRequest request) {
        Role role = Role.builder()
                .code(request.getCode())
                .build();

        return save(role);
    }

    public Role update(Long id, RoleRequest request) {
        Role role = findById(id);
        role.setCode(request.getCode());
        return save(role);
    }

    public void delete(Long id) {
        Role role = findById(id);
        roleRepository.delete(role);
    }
}
