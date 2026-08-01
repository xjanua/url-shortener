package me.xjanua.spring.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.model.User;
import me.xjanua.spring.backend.model.UserRole;
import me.xjanua.spring.backend.repository.UserRoleRepository;

@Service
@RequiredArgsConstructor
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;

    public List<UserRole> findByUser(User user) {
        return userRoleRepository.findByUser(user);
    }
}
