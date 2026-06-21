package com.lucan.community.service;

import com.lucan.community.dto.user.*;
import com.lucan.community.entity.User;
import com.lucan.community.exception.ConflictException;
import com.lucan.community.exception.UnauthorizedException;
import com.lucan.community.message.MessageCode;
import com.lucan.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        validatePasswordMatch(request.getPassword(), request.getPasswordConfirm());
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException(MessageCode.EMAIL_ALREADY_EXISTS.getMessage());
        }
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new ConflictException(MessageCode.NICKNAME_ALREADY_EXISTS.getMessage());
        }

        User user = new User(
                request.getEmail(),
                request.getPassword(),
                request.getNickname(),
                request.getProfileImage()
        );

        User savedUser = userRepository.save(user);

        return new SignupResponse(savedUser.getUserId());
    }

    public LoginResponse login(LoginRequest request) {

        User savedUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException(MessageCode.LOGIN_FAILED.getMessage()));

        if (!savedUser.getPassword().equals(request.getPassword())) {
            throw new UnauthorizedException(MessageCode.LOGIN_FAILED.getMessage());
        }

        return new LoginResponse(savedUser.getUserId());
    }

    @Transactional
    public void updateUser(Long userId, UserUpdateRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException(MessageCode.LOGIN_REQUIRED.getMessage()));

        if (userRepository.existsByNickname(request.getNickname())) {
            throw new ConflictException(MessageCode.NICKNAME_ALREADY_EXISTS.getMessage());
        }

        user.setNickname(request.getNickname());
        user.setProfileImage(request.getProfileImage());
    }

    @Transactional
    public void updatePassword(Long userId, PasswordUpdateRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException(MessageCode.LOGIN_REQUIRED.getMessage()));

        validatePasswordMatch(request.getPassword(), request.getPasswordConfirm());

        user.setPassword(request.getPassword());
    }

    @Transactional
    public void deleteUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException(MessageCode.LOGIN_REQUIRED.getMessage()));

        userRepository.delete(user);
    }

    public void logout(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new UnauthorizedException(MessageCode.LOGIN_REQUIRED.getMessage());
        }
    }

    private void validatePasswordMatch(String password, String passwordConfirm) {
        if (!password.equals(passwordConfirm)) {
            throw new IllegalArgumentException(MessageCode.PASSWORD_NOT_MATCH.getMessage());
        }
    }
}