package com.kakao_tech.community.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kakao_tech.community.entity.Session;
import com.kakao_tech.community.entity.User;
import com.kakao_tech.community.exception.CustomErrorCode;
import com.kakao_tech.community.exception.RestApiException;
import com.kakao_tech.community.provider.SessionProvider;
import com.kakao_tech.community.repository.SessionRepository;
import com.kakao_tech.community.repository.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewUserSerivce {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final SessionProvider sessionProvider;
    private final PasswordEncoder passwordEncoder;

    private static final int SESSION_EXPIRE_MINUTES = 30;

    public User getUser(Integer userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Transactional
    public String signInUser(String email, String password, HttpServletResponse response) {
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null || !checkPassword(user, password)) {
            throw new RestApiException(CustomErrorCode.DIFFERENT_SIGN_INFO);
        }

        sessionRepository.deleteByUserId(user.getId());

        var sidResponse = generateAndSaveSessionId(user);

        addSessionIdCookies(response, sidResponse);

        return sidResponse.sid();
    }

    public void signOutUser(HttpServletResponse response) {
        addSessionIdCookies(response, "sid", null, 0);
    }

    private void addSessionIdCookies(HttpServletResponse response, SessionIdResponse sidResponse) {
        addSessionIdCookies(response, "sid", sidResponse.sid(), SESSION_EXPIRE_MINUTES);
    }

    private void addSessionIdCookies(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge * 60); // 여기가 초가 기준이라서 60곱해줘야 30분동안 쿠기가 유지됨.
        response.addCookie(cookie);
    }


    private boolean checkPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    private SessionIdResponse generateAndSaveSessionId(User user) {
        String sid = sessionProvider.createSession();

        Session sessionEntity = new Session();
        sessionEntity.setUser(user);
        sessionEntity.setSid(sid);
        sessionEntity.setCreatedAt(java.time.LocalDateTime.now());
        sessionEntity.setExpiredAt(java.time.LocalDateTime.now().plusMinutes(SESSION_EXPIRE_MINUTES));
        sessionRepository.save(sessionEntity);
    
        return new SessionIdResponse(sid);
    }

    public record SessionIdResponse(String sid) {}
}
