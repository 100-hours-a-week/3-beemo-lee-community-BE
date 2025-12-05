package com.kakao_tech.community.service;

import com.kakao_tech.community.dto.user.SignUpDTO;
import com.kakao_tech.community.dto.user.UserDTO;
import com.kakao_tech.community.entity.User;
import com.kakao_tech.community.exception.code.AuthErrorCode;
import com.kakao_tech.community.exception.common.RestApiException;
import com.kakao_tech.community.repository.RefreshTokenRepository;
import com.kakao_tech.community.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ImageService imageService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("회원 조회: 성공")
    void getUserById_Success() {
        // given
        Integer userId = 1;
        User user = new User("테스트유저", "test@example.com", "encodedPassword123!");
        user.setProfileUrl("https://example.com/profile.jpg");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        UserDTO.Response response = userService.getUserById(userId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getNickname()).isEqualTo("테스트유저");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getProfileUrl()).isEqualTo("https://example.com/profile.jpg");

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("회원 조회: 존재하지 않으면 USER_NOT_FOUND 예외 발생")
    void getUserById_NotFound() {
        // given
        Integer userId = 999;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(RestApiException.class)
                .satisfies(ex -> {
                    RestApiException exception = (RestApiException) ex;
                    assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.USER_NOT_FOUND);
                });

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("회원가입: 닉네임 중복 시 DUPLICATE_NICKNAME 예외 발생")
    void createUser_DuplicateNickname() {
        // given
        SignUpDTO.Request request = new SignUpDTO.Request(
                "중복닉네임",
                "test@example.com",
                "Password123!"
        );

        when(userRepository.existsByNickname("중복닉네임")).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.createUser(request, null))
                .isInstanceOf(RestApiException.class)
                .satisfies(ex -> {
                    RestApiException exception = (RestApiException) ex;
                    assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.DUPLICATE_NICKNAME);
                });

        verify(userRepository, times(1)).existsByNickname("중복닉네임");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입: 이메일 중복 시 DUPLICATE_EMAIL 예외 발생")
    void createUser_DuplicateEmail() {
        // given
        SignUpDTO.Request request = new SignUpDTO.Request(
                "새유저",
                "duplicate@example.com",
                "Password123!"
        );

        when(userRepository.existsByNickname("새유저")).thenReturn(false);
        when(userRepository.existsByEmail("duplicate@example.com")).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.createUser(request, null))
                .isInstanceOf(RestApiException.class)
                .satisfies(ex -> {
                    RestApiException exception = (RestApiException) ex;
                    assertThat(exception.getErrorCode()).isEqualTo(AuthErrorCode.DUPLICATE_EMAIL);
                });

        verify(userRepository, times(1)).existsByNickname("새유저");
        verify(userRepository, times(1)).existsByEmail("duplicate@example.com");
        verify(userRepository, never()).save(any(User.class));
    }
}
