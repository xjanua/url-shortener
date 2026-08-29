package me.xjanua.spring.backend.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import me.xjanua.spring.backend.dto.user.UserDetailResponse;
import me.xjanua.spring.backend.mapper.UserMapper;
import me.xjanua.spring.backend.model.User;
import me.xjanua.spring.backend.service.UserService;

class UserControllerTest {

    private UserService userService;
    private UserMapper userMapper;
    private UserController controller;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        userMapper = mock(UserMapper.class);
        controller = new UserController(userService, userMapper);
    }

    @Test
    void getCurrentUserReturnsAuthenticatedUserDetails() {
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .email("user@example.com")
                .build();
        UserDetailResponse response = new UserDetailResponse();
        response.setId(userId);
        response.setEmail("user@example.com");

        when(userService.fetchCurrentUser()).thenReturn(user);
        when(userMapper.toDetailResponse(user)).thenReturn(response);

        ResponseEntity<UserDetailResponse> result = controller.getCurrentUser();

        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(result.getBody()).isSameAs(response);
        verify(userService).fetchCurrentUser();
        verify(userMapper).toDetailResponse(user);
    }
}
