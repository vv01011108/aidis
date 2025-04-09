package com.example.book_manage.user.controller;

import com.example.book_manage.user.db.UserEntity;
import com.example.book_manage.user.exception.UserException;
import com.example.book_manage.user.model.UserLoginRequest;
import com.example.book_manage.user.model.UserRegisterRequest;
import com.example.book_manage.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegisterRequest userRegisterRequest) {
        try {
            String response = userService.registerUser(userRegisterRequest);
            return ResponseEntity.ok(response); // 성공적으로 등록된 경우
        } catch (UserException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); // 오류 발생 시
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginRequest loginRequest, HttpSession session) {
        UserEntity user = userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword());
        if (user != null) {
            session.setAttribute("user", user); // 세션에 사용자 추가
            return ResponseEntity.ok("로그인 성공"); // 성공 응답
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패"); // 실패 응답
        }

    }

    // 로그아웃 처리
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate(); // 세션 무효화
        return ResponseEntity.ok("로그아웃 성공");
    }

    @GetMapping("/some-endpoint")
    public ResponseEntity<String> someMethod(@SessionAttribute(name = "userEntity", required = false) UserEntity userEntity) {

        if (userEntity == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 필요");
        }
        return ResponseEntity.ok("사용자 정보: " + userEntity.getEmail());
    }
}
