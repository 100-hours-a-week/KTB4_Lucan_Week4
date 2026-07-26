import com.lucan.community.dto.user.*;
import com.lucan.community.entity.User;
import com.lucan.community.exception.ConflictException;
import com.lucan.community.exception.UnauthorizedException;
import com.lucan.community.repository.UserRepository;
import com.lucan.community.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("회원가입 성공")
    void signupSuccess() {
        SignupRequest request = new SignupRequest(
                "test@test.com",
                "password123",
                "password123",
                "lucan",
                null
        );

        given(userRepository.existsByEmail(request.getEmail()))
                .willReturn(false);

        given(userRepository.existsByNickname(request.getNickname()))
                .willReturn(false);

        given(passwordEncoder.encode(request.getPassword()))
                .willReturn("encodedPassword");

        User savedUser = new User(
                request.getEmail(),
                "encodedPassword",
                request.getNickname(),
                request.getProfileImage()
        );

        given(userRepository.save(any(User.class)))
                .willReturn(savedUser);

        SignupResponse response = userService.signup(request);

        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("이메일 중복 회원가입 실패")
    void signupFailWhenEmailDuplicated() {

        SignupRequest request = new SignupRequest(
                "test@test.com",
                "password123",
                "password123",
                "lucan",
                null
        );

        given(userRepository.existsByEmail(request.getEmail()))
                .willReturn(true);

        assertThatThrownBy(() -> userService.signup(request))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    @DisplayName("닉네임 중복 회원가입 실패")
    void signupFailWhenNicknameDuplicated() {

        SignupRequest request = new SignupRequest(
                "test@test.com",
                "password123",
                "password123",
                "lucan",
                null
        );

        given(userRepository.existsByEmail(request.getEmail()))
                .willReturn(false);

        given(userRepository.existsByNickname(request.getNickname()))
                .willReturn(true);

        assertThatThrownBy(() -> userService.signup(request))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    @DisplayName("비밀번호와 비밀번호 확인이 다르면 회원가입 실패")
    void signupFailWhenPasswordNotMatch() {

        SignupRequest request = new SignupRequest(
                "test@test.com",
                "password123",
                "differentPassword",
                "lucan",
                null
        );

        assertThatThrownBy(() -> userService.signup(request))
                .isInstanceOf(IllegalArgumentException.class);
    }



    @Test
    @DisplayName("회원 정보 수정 성공")
    void updateUserSuccess() {

        Long userId = 1L;

        User user = new User(
                "test@test.com",
                "encodedPassword",
                "lucan",
                "oldImage.jpg"
        );

        UserUpdateRequest request = new UserUpdateRequest(
                "새닉네임",
                "newImage.jpg"
        );

        given(userRepository.findById(userId))
                .willReturn(Optional.of(user));

        given(userRepository.existsByNickname(request.getNickname()))
                .willReturn(false);

        userService.updateUser(userId, request);

        assertThat(user.getNickname())
                .isEqualTo("새닉네임");

        assertThat(user.getProfileImage())
                .isEqualTo("newImage.jpg");
    }

    @Test
    @DisplayName("존재하지 않는 회원이면 회원 정보 수정 실패")
    void updateUserFailWhenUserNotFound() {

        Long userId = 1L;

        UserUpdateRequest request = new UserUpdateRequest(
                "새닉네임",
                "newImage.jpg"
        );

        given(userRepository.findById(userId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(userId, request))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("이미 사용 중인 닉네임이면 회원 정보 수정 실패")
    void updateUserFailWhenNicknameDuplicated() {

        Long userId = 1L;

        User user = new User(
                "test@test.com",
                "encodedPassword",
                "lucan",
                null
        );

        UserUpdateRequest request = new UserUpdateRequest(
                "중복닉네임",
                null
        );

        given(userRepository.findById(userId))
                .willReturn(Optional.of(user));

        given(userRepository.existsByNickname(request.getNickname()))
                .willReturn(true);

        assertThatThrownBy(() -> userService.updateUser(userId, request))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    void updatePasswordSuccess() {

        Long userId = 1L;

        User user = new User(
                "test@test.com",
                "encodedOldPassword",
                "lucan",
                null
        );

        PasswordUpdateRequest request = new PasswordUpdateRequest(
                "oldPassword",
                "newPassword",
                "newPassword"
        );

        given(userRepository.findById(userId))
                .willReturn(Optional.of(user));

        given(passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword()
        )).willReturn(true);

        given(passwordEncoder.encode(request.getPassword()))
                .willReturn("encodedNewPassword");

        userService.updatePassword(userId, request);

        assertThat(user.getPassword()).isEqualTo("encodedNewPassword");
    }

    @Test
    @DisplayName("존재하지 않는 회원이면 비밀번호 변경 실패")
    void updatePasswordFailWhenUserNotFound() {

        Long userId = 1L;

        PasswordUpdateRequest request = new PasswordUpdateRequest(
                "oldPassword",
                "newPassword",
                "newPassword"
        );

        given(userRepository.findById(userId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updatePassword(userId, request))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("현재 비밀번호 불일치 비밀번호 변경 실패")
    void updatePasswordFailWhenCurrentPasswordNotMatch() {

        Long userId = 1L;

        User user = new User(
                "test@test.com",
                "encodedOldPassword",
                "lucan",
                null
        );

        PasswordUpdateRequest request = new PasswordUpdateRequest(
                "wrongPassword",
                "newPassword",
                "newPassword"
        );

        given(userRepository.findById(userId))
                .willReturn(Optional.of(user));

        given(passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword()
        )).willReturn(false);

        assertThatThrownBy(() -> userService.updatePassword(userId, request))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("새 비밀번호와 비밀번호 확인 불일치 비밀번호 변경 실패")
    void updatePasswordFailWhenPasswordNotMatch() {

        Long userId = 1L;

        User user = new User(
                "test@test.com",
                "encodedOldPassword",
                "lucan",
                null
        );

        PasswordUpdateRequest request = new PasswordUpdateRequest(
                "oldPassword",
                "newPassword",
                "differentPassword"
        );

        given(userRepository.findById(userId))
                .willReturn(Optional.of(user));

        given(passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword()
        )).willReturn(true);

        assertThatThrownBy(() -> userService.updatePassword(userId, request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("회원 탈퇴 성공")
    void deleteUserSuccess() {

        Long userId = 1L;

        User user = new User(
                "test@test.com",
                "encodedPassword",
                "lucan",
                null
        );

        given(userRepository.findById(userId))
                .willReturn(Optional.of(user));

        userService.deleteUser(userId);

        assertThat(user.isDeleted()).isTrue();
        assertThat(user.getNickname()).isEqualTo("탈퇴 유저");
    }

    @Test
    @DisplayName("존재하지 않는 회원 회원 탈퇴 실패")
    void deleteUserFailWhenUserNotFound() {

        Long userId = 1L;

        given(userRepository.findById(userId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(userId))
                .isInstanceOf(UnauthorizedException.class);
    }
}

