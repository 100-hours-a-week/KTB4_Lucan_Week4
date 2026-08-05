package com.lucan.community.controller;

import com.lucan.community.dto.response.ApiResponse;
import com.lucan.community.dto.user.*;
import com.lucan.community.message.MessageCode;
import com.lucan.community.security.CustomUserDetails;
import com.lucan.community.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup")
    public ApiResponse signup(@Valid @ModelAttribute SignupRequest request) {
        SignupResponse response = userService.signup(request);

        return new ApiResponse(MessageCode.REGISTER_SUCCESS.getMessage(), response);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/me")
    public ApiResponse getMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        LoginResponse response = userService.getMyInfo(userDetails.getUserId());

        return new ApiResponse(MessageCode.GET_USER_SUCCESS.getMessage(), response);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse updateUser(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(required = false) String nickname,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        UserUpdateRequest request = new UserUpdateRequest(nickname, profileImage);

        userService.updateUser(userDetails.getUserId(), request);

        return new ApiResponse(MessageCode.USER_UPDATE_SUCCESS.getMessage(), null);
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/me/password")
    public ApiResponse updatePassword(@AuthenticationPrincipal CustomUserDetails userDetails,
                                      @Valid @RequestBody PasswordUpdateRequest request) {
        userService.updatePassword(userDetails.getUserId(), request);

        return new ApiResponse(MessageCode.PASSWORD_UPDATE_SUCCESS.getMessage(), null);
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/me")
    public ApiResponse deleteUser(@AuthenticationPrincipal CustomUserDetails userDetails, HttpServletRequest request) {
        userService.deleteUser(userDetails.getUserId());

        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        return new ApiResponse(MessageCode.USER_DELETE_SUCCESS.getMessage(), null);
    }
}