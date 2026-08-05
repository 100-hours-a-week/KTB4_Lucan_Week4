package com.lucan.community.service;

import com.lucan.community.dto.user.*;
import com.lucan.community.entity.User;
import com.lucan.community.exception.ConflictException;
import com.lucan.community.exception.UnauthorizedException;
import com.lucan.community.message.MessageCode;
import com.lucan.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        validatePasswordMatch(request.getPassword(), request.getPasswordConfirm());
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException(MessageCode.EMAIL_ALREADY_EXISTS.getMessage());
        }
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new ConflictException(MessageCode.NICKNAME_ALREADY_EXISTS.getMessage());
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        String profileImageUrl = null;

        if (request.getProfileImage() != null && !request.getProfileImage().isEmpty()) {
            profileImageUrl = s3Service.uploadImage(request.getProfileImage(),"profiles");
        }

        User user = new User(
                request.getEmail(),
                encodedPassword,
                request.getNickname(),
                profileImageUrl
        );

        User savedUser = userRepository.save(user);

        return new SignupResponse(savedUser.getUserId());
    }

    @Transactional(readOnly = true)
    public LoginResponse getMyInfo(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                        new UnauthorizedException(MessageCode.LOGIN_REQUIRED.getMessage()));

        return new LoginResponse(user.getUserId(), user.getEmail(), user.getNickname(), user.getProfileImage());
    }

    @Transactional
    public void updateUser(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException(MessageCode.LOGIN_REQUIRED.getMessage()));

        String nickname = request.getNickname();

        boolean hasNickname = nickname != null && !nickname.isBlank();

        boolean hasProfileImage = request.getProfileImage() != null && !request.getProfileImage().isEmpty();

        // 닉네임과 사진 모두 전송되지 않은 경우
        if (!hasNickname && !hasProfileImage) {
            throw new IllegalArgumentException(MessageCode.INVALID_REQUEST.getMessage());
        }

        // 닉네임이 전송된 경우
        if (hasNickname) {
            String trimmedNickname = nickname.trim();

            if (trimmedNickname.equals(user.getNickname())) {
                // 같은 닉네임만 보내고 사진도 바꾸지 않은 경우
                if (!hasProfileImage) {
                    throw new IllegalArgumentException(MessageCode.SAME_NICKNAME.getMessage());
                }

                // 새 사진이 있다면 닉네임은 그대로 두고
                // 아래 이미지 로직만 실행
            } else {
                if (userRepository.existsByNickname(trimmedNickname)) {
                    throw new ConflictException(MessageCode.NICKNAME_ALREADY_EXISTS.getMessage());
                }

                user.setNickname(trimmedNickname);
            }
        }

        // 새 프로필 사진이 전송된 경우
        if (hasProfileImage) {
            String oldProfileImageUrl = user.getProfileImage();

            String newProfileImageUrl =s3Service.uploadImage(request.getProfileImage(), "profiles");

            // User 엔티티에는 URL 문자열 저장
            user.setProfileImage(newProfileImageUrl);

            // 기존 이미지가 있었다면 S3에서 삭제
            if (oldProfileImageUrl != null && !oldProfileImageUrl.isBlank()) {
                s3Service.deleteImage(oldProfileImageUrl);
            }
        }
    }

    @Transactional
    public void updatePassword(Long userId, PasswordUpdateRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException(MessageCode.LOGIN_REQUIRED.getMessage()));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new UnauthorizedException(MessageCode.CURRENT_PASSWORD_NOT_MATCH.getMessage());
        }

        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException(MessageCode.SAME_AS_CURRENT_PASSWORD.getMessage());
        }

        validatePasswordMatch(request.getPassword(), request.getPasswordConfirm());

        user.setPassword(passwordEncoder.encode(request.getPassword()));
    }

    @Transactional
    public void deleteUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException(MessageCode.LOGIN_REQUIRED.getMessage()));

        String profileImageUrl = user.getProfileImage();

        if (
                profileImageUrl != null && !profileImageUrl.isBlank()
        ) {
            s3Service.deleteImage(profileImageUrl);

            user.setProfileImage(null);
        }

        user.delete();
    }

    private void validatePasswordMatch(String password, String passwordConfirm) {
        if (!password.equals(passwordConfirm)) {
            throw new IllegalArgumentException(MessageCode.PASSWORD_NOT_MATCH.getMessage());
        }
    }
}