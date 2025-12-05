package com.kakao_tech.community.controller;

import com.kakao_tech.community.dto.user.SignUpDTO;
import com.kakao_tech.community.dto.user.UserDTO;
import com.kakao_tech.community.service.UserService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

    public final UserService userService;

    @PostMapping("/signin")
    public ResponseEntity<?> signInUser(
            @RequestBody Map<String, String> body,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {

        String accessToken = userService.signInUser(body.get("email"), body.get("password"), response);

        if (accessToken == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "아이디또는 비밀번호가 잘못되었습니다.");
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.status(201).body(Map.of("message", "로그인 성공"));
    }

    @PostMapping("/refresh")
    @ResponseBody
    public Map<String, String> refresh(@CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {

        if (refreshToken == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return Map.of("error", "Refresh token missing");
        }

        try {
            var tokenRes = userService.refreshTokens(refreshToken, response);

            if (tokenRes == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return Map.of("error", "Refresh token invalid or expired");
            }

            return Map.of(
                    "accessToken", tokenRes.accessToken(),
                    "refreshToken", tokenRes.refreshToken());
        } catch (ResponseStatusException exception) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return Map.of("error", "Refresh token invalid or expired");
        }
    }

    @PostMapping("/signout")
    public ResponseEntity<?> signOutUserRefresh(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {
        userService.signOutUser(response, refreshToken);
        return ResponseEntity.ok().body(Map.of("message", "로그아웃 성공"));
    }

    // 회원가입
    @PostMapping("/users")
    public ResponseEntity<?> createUser(
            @Valid @RequestPart(value = "user") SignUpDTO.Request user,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {

        SignUpDTO.Response result = userService.createUser(user, profileImage);

        return ResponseEntity.status(201).body(result);
    }

    // 회원 조회
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserDTO.Response> getUser(@PathVariable Integer userId) {
        UserDTO.Response response = userService.getUserById(userId);
        return ResponseEntity.ok(response);
    }

    // 회원 정보 수정
    @PatchMapping("/users/{userId}")
    public ResponseEntity<UserDTO.UpdateResponse> updateUser(
            @PathVariable Integer userId,
            @RequestAttribute("userId") Integer currentUserId,
            @RequestPart(value = "user", required = false) UserDTO.UpdateRequest request,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {

        // 본인만 수정 가능
        if (!userId.equals(currentUserId)) {
            return ResponseEntity.status(403).build();
        }

        UserDTO.UpdateResponse response = userService.updateUser(userId, request, profileImage);
        return ResponseEntity.ok(response);
    }

    // 비밀번호 변경
    @PatchMapping("/users/{userId}/password")
    public ResponseEntity<UserDTO.PasswordUpdateResponse> updatePassword(
            @PathVariable Integer userId,
            @RequestAttribute("userId") Integer currentUserId,
            @RequestBody UserDTO.PasswordUpdateRequest request) {

        // 본인만 수정 가능
        if (!userId.equals(currentUserId)) {
            return ResponseEntity.status(403).build();
        }

        UserDTO.PasswordUpdateResponse response = userService.updatePassword(userId, request);
        return ResponseEntity.ok(response);
    }

    // 회원 탈퇴
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Map<String, String>> deleteUser(
            @PathVariable Integer userId,
            @RequestAttribute("userId") Integer currentUserId,
            HttpServletResponse response) {

        // 본인만 탈퇴 가능
        if (!userId.equals(currentUserId)) {
            return ResponseEntity.status(403).build();
        }

        userService.deleteUser(userId, response);
        return ResponseEntity.ok(Map.of("message", "회원 탈퇴가 완료되었어요."));
    }

}
