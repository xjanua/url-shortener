package me.xjanua.spring.backend.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import me.xjanua.spring.backend.dto.PaginationDTO;
import me.xjanua.spring.backend.dto.user.UserDetailResponse;
import me.xjanua.spring.backend.dto.user.UserRegisterRequest;
import me.xjanua.spring.backend.enums.ErrorCode;
import me.xjanua.spring.backend.exception.BadRequestException;
import me.xjanua.spring.backend.exception.NotFoundException;
import me.xjanua.spring.backend.mapper.UserMapper;
import me.xjanua.spring.backend.model.User;
import me.xjanua.spring.backend.repository.UserRepository;
import me.xjanua.spring.backend.util.PaginationUtil;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND.name()));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND.name()));
    }

    public User register(UserRegisterRequest request) {
        if (isEmailExists(request.getEmail())) {
            throw new BadRequestException(ErrorCode.EMAIL_ALREADY_EXISTS.name());
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException(ErrorCode.PASSWORD_MISMATCH.name());
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        userRepository.save(user);
        return user;
    }

    public PaginationDTO.Response fetchAll(Specification<User> spec, Pageable pageable) {
        Page<User> users = userRepository.findAll(spec, pageable);

        PaginationDTO.Info info = PaginationUtil.buildInfo(users, pageable);

        List<UserDetailResponse> responses = users.getContent().stream()
                .map(userMapper::toDetailResponse)
                .collect(Collectors.toList());

        PaginationDTO.Response response = new PaginationDTO.Response();
        response.setInfo(info);
        response.setResponse(responses);

        return response;
    }

    private boolean isEmailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}