package com.kakao_tech.community.provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.kakao_tech.community.entity.Session;
import com.kakao_tech.community.exception.CustomErrorCode;
import com.kakao_tech.community.exception.RestApiException;
import com.kakao_tech.community.repository.SessionRepository;

import java.util.UUID;

@Component
public class SessionProvider {
    @Autowired
    private SessionRepository sessionRepository;

    public String createSession() {
        String sid = UUID.randomUUID().toString();
        return sid;
    }

    public void validate(String sid) {
        Session session = sessionRepository.findBySid(sid);

        if (session == null) {
            throw new RestApiException(CustomErrorCode.INVALID_SESSIONID);
        }
    }
}