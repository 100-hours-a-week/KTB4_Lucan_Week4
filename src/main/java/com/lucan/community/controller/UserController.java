package com.lucan.community.controller;

import com.lucan.community.dto.response.ApiResponse;
import com.lucan.community.dto.user.*;
import com.lucan.community.message.MessageCode;
import com.lucan.community.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup")
    public ApiResponse signup(@Valid @RequestBody SignupRequest request) {
        SignupResponse response = userService.signup(request);

        return new ApiResponse(MessageCode.REGISTER_SUCCESS.getMessage(), response);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    public ApiResponse login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);

        return new ApiResponse(MessageCode.LOGIN_SUCCESS.getMessage(), response);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{userId}")
    public ApiResponse updateUser(@PathVariable Long userId, @Valid @RequestBody UserUpdateRequest request) {
        userService.updateUser(userId, request);

        return new ApiResponse(MessageCode.USER_UPDATE_SUCCESS.getMessage(), null);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{userId}/password")
    public ApiResponse updatePassword(@PathVariable Long userId, @Valid @RequestBody PasswordUpdateRequest request) {
        userService.updatePassword(userId, request);

        return new ApiResponse(MessageCode.PASSWORD_UPDATE_SUCCESS.getMessage(), null);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/{userId}")
    public ApiResponse deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);

        return new ApiResponse(MessageCode.USER_DELETE_SUCCESS.getMessage(), null);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/{userId}/logout")
    public ApiResponse logout(@PathVariable Long userId) {
        userService.logout(userId);

        return new ApiResponse(MessageCode.LOGOUT_SUCCESS.getMessage(), null);
    }
}