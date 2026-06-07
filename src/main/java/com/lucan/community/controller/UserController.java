package com.lucan.community.controller;

import com.lucan.community.dto.response.ApiResponse;
import com.lucan.community.dto.user.PasswordUpdateRequest;
import com.lucan.community.dto.user.SignupRequest;
import com.lucan.community.dto.user.LoginRequest;
import com.lucan.community.dto.user.UserUpdateRequest;
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
        return userService.signup(request);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/login")
    public ApiResponse login(@Valid @RequestBody LoginRequest request){
        return userService.login(request);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{userId}")
    public ApiResponse updateUser(@PathVariable Integer userId, @Valid @RequestBody UserUpdateRequest request) {
        return userService.updateUser(userId, request);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/{userId}/password")
    public ApiResponse updatePassword(@PathVariable Integer userId, @Valid @RequestBody PasswordUpdateRequest request) {
        return userService.updatePassword(userId, request);
    }

    @DeleteMapping("/{userId}")
    public ApiResponse deleteUser(@PathVariable Integer userId) {
        return userService.deleteUser(userId);
    }

    @PostMapping("/{userId}/logout")
    public ApiResponse logout(@PathVariable Integer userId) {
        return userService.logout(userId);
    }
}