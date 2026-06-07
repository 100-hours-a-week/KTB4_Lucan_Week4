package com.lucan.community.service;

import com.lucan.community.dto.response.ApiResponse;
import com.lucan.community.dto.user.*;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public ApiResponse signup(SignupRequest request) {

        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new IllegalArgumentException("password_not_match");
        }

        if ("test@test.com".equals(request.getEmail())) {
            throw new IllegalArgumentException("email_already_exists");
        }

        if ("lucan".equals(request.getNickname())) {
            throw new IllegalArgumentException("nickname_already_exists");
        }

        SignupResponse response = new SignupResponse(1);

        return new ApiResponse(
                "register_success",
                response
        );
    }

    public ApiResponse login(LoginRequest request) {

        if (!"test@test.com".equals(request.getEmail()) || !"12345678".equals(request.getPassword())) {
            throw new IllegalArgumentException("login_failed");
        }

        LoginResponse response = new LoginResponse(1);

        return new ApiResponse("login_success", response);
    }

    public ApiResponse updateUser(Integer userId, UserUpdateRequest request) {

        if ("lucan".equals(request.getNickname())) {
            throw new IllegalArgumentException("nickname_already_exists");
        }

        return new ApiResponse("user_update_success", null);
    }

    public ApiResponse updatePassword(Integer userId, PasswordUpdateRequest request) {

        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new IllegalArgumentException("password_not_match");
        }

        return new ApiResponse("password_update_success", null);
    }

    public ApiResponse deleteUser(Integer userId) {

        if (userId != 1) {
            throw new IllegalArgumentException("login_required");
        }

        return new ApiResponse("user_delete_success", null);
    }

    public ApiResponse logout(Integer userId) {

        if (userId != 1) {
            throw new IllegalArgumentException("login_required");
        }

        return new ApiResponse("logout_success", null);
    }
}