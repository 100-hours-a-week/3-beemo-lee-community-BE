package com.kakao_tech.community.controller;

import com.kakao_tech.community.dto.UserDTO;
import com.kakao_tech.community.service.NewUserSerivce;
import com.kakao_tech.community.service.UserService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    public final UserService userService;
    public final NewUserSerivce newUserService;

    @PostMapping("/signIn")
    public ResponseEntity<?> signInUser(
            @RequestBody Map<String, String> body,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {

        newUserService.signInUser(body.get("email"),body.get("password"), response);

        return ResponseEntity.ok().body(Map.of("message", "로그인 성공"));
    }

    // 회원가입 기능
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestPart("user") Map<String, String> body,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {

        UserDTO.SignUpRequest userDTO = new UserDTO.SignUpRequest(
                body.get("nickname"),
                body.get("email"),
                        body.get("password")
                );

        String image = body.get("profile");
        System.out.println(image);
        
        UserDTO.SignUpResponse result = userService.createUser(userDTO, profileImage);
        return ResponseEntity.status(201).body(result);
    }

}
