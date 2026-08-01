package me.xjanua.spring.backend.service;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.dto.UserDetailsCustom;
import me.xjanua.spring.backend.model.Role;
import me.xjanua.spring.backend.model.User;
import me.xjanua.spring.backend.enums.UserStatus;

@Service
@RequiredArgsConstructor
public class UserDetailsCustomService implements UserDetailsService {

    private final UserService userService;
    private final UserRoleService userRoleService;
    private final RolePermissionService rolePermissionService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        boolean enabled = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;

        User user = userService.findByEmail(email);

        if (user.getStatus() == UserStatus.BLOCKED) {
            accountNonLocked = false;
        }

        List<SimpleGrantedAuthority> authorities = getAuthorities(user);

        return new UserDetailsCustom(
                user,
                user.getEmail(),
                user.getPassword(),
                enabled,
                accountNonExpired,
                credentialsNonExpired,
                accountNonLocked,
                authorities);
    }

    private List<SimpleGrantedAuthority> getAuthorities(User user) {
        List<Role> roles = userRoleService.findByUser(user)
                .stream()
                .map(userRole -> userRole.getRole())
                .toList();

        return rolePermissionService.findPermissionCodesByRoles(roles)
                .stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
}
