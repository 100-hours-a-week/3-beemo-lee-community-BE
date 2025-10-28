package com.kakao_tech.community.controller;

import com.kakao_tech.community.dto.UserDTO;
import com.kakao_tech.community.service.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

    public final UserService userService;

    // 회원가입 기능
    @PostMapping("/api/users")
    public ResponseEntity<?> createUser(@RequestPart("user") Map<String, String> body, @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {

        UserDTO.SignUpRequest userDTO =
                new UserDTO.SignUpRequest(
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
