package com.kakao_tech.community.provider;

import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
public class SessionProvider {
    // 세션 기반에서 세션 아이디는 어떻게 이루어지는게 보통일까?
    // JWT처럼 추측이 불가능한 생성 방식을 활용하기 위해서 비밀키로 서명을 하는 그런 정보를 포함시켜야 하는건가?

    // private final Key key = Keys.hmacShaKeyFor(
    //         Base64.getDecoder().decode("YWRhcHRlcnphZGFwdGVyemFkYXB0ZXJ6YWRhcHRlcnphZGFwdGVyeg==") // adapterzadapterzadapterzadapterzadapterz
    // );

    public String createSession() {
        String sid = UUID.randomUUID().toString();
        return sid;
    }

    public String parse(String sid) {
        // return sid.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwt);
        // Todo : 세션 아이디를 어떻게 파싱하지? 일단 Null 보내...
        return null;
    }
}