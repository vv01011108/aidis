package com.example.social.controller;

import com.example.social.dto.LoginRequestDTO;
import com.example.social.dto.SignupRequestDTO;
import com.example.social.entity.User;
import com.example.social.service.UserService;
import com.example.social.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // private final JwtUtil jwtUtil = new JwtUtil();

    // 회원가입 처리
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestParam("name") String name,
                                         @RequestParam("email") String email,
                                         @RequestParam("password") String password,
                                         @RequestParam("photo") MultipartFile photo,
                                         @RequestParam("interest") String interest,
                                         @RequestParam("workplace") String workplace,
                                         @RequestParam("phoneNumber") String phoneNumber) throws IOException {
        // 사진 파일을 저장
        String photoPath = savePhoto(photo);

        // DTO로 변환
        SignupRequestDTO signupRequestDTO = new SignupRequestDTO(name, email, password, photoPath, interest, workplace, phoneNumber);

        try {
            // 회원가입 처리
            String result = userService.registerUser(signupRequestDTO);
            if (result.equals("회원가입 성공!")) {
                return ResponseEntity.status(200).body(result);
            } else {
                return ResponseEntity.status(400).body(result);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("회원가입 중 오류 발생: " + e.getMessage());
        }
    }

    private String savePhoto(MultipartFile photo) throws IOException {

        // 파일 이름 중복 방지를 위해 UUID를 사용
        String filename = UUID.randomUUID().toString() + "_" + photo.getOriginalFilename();
        String uploadDir = "uploads/";
        Path path = Paths.get(uploadDir + filename);

        Files.createDirectories(path.getParent());
        Files.write(path, photo.getBytes());

        // 반환 경로를 URL로 변환
        return "http://localhost:8080/" + uploadDir + filename;
    }

    // 로그인 처리
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO requestDTO, HttpSession session) {
        User user = userService.authenticate(requestDTO.getEmail(), requestDTO.getPassword());
        if (user != null) {
            session.setAttribute("user", user); // 세션에 사용자 정보 저장
            return ResponseEntity.ok("로그인 성공");
        }
        else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패");
        }
    }

    // 로그인한 사용자 정보 반환
    @GetMapping("/me")
    public ResponseEntity<Object> getUserInfo(HttpSession session) {
        Object user = session.getAttribute("user");
        if (user != null) {
            return ResponseEntity.status(200).body(user); // 세션에 저장된 사용자 정보 반환
        } else {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }
    }

    // 사용자 이름으로 검색
    @GetMapping("/search")
    public List<User> searchUsers(@RequestParam String name) {
        return userService.searchUsersByName(name);
    }

    @GetMapping("/uploads/{filename}")
    public ResponseEntity<Resource> getPhoto(@PathVariable String filename) {

        // 파일이 저장된 경로
        String uploadDir = "uploads/";

        // 파일을 파일 시스템 리소스로 반환
        FileSystemResource resource = new FileSystemResource(uploadDir + filename);

        if (resource.exists()) {
            return ResponseEntity.ok().body(resource);
        } else {

            return ResponseEntity.status(404).body(null); // 파일이 없으면 404 반환

        }
    }

    // 특정 사용자 정보 조회
    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserInfoById(@PathVariable Long userId) {
        try {
            // 사용자 정보 조회
            User userEntity = userService.getUserById(userId);

            if (userEntity != null) {
                return ResponseEntity.status(200).body(userEntity); // 사용자 정보 반환
            } else {
                return ResponseEntity.status(404).body("사용자를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("사용자 정보 조회 중 오류 발생: " + e.getMessage());
        }
    }

    // 사용자 정보 수정
    @PutMapping("/{userId}/edit")
    public ResponseEntity<String> editUserInfo(
            @PathVariable Long userId,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "interest", required = false) String interest,
            @RequestParam(value = "workplace", required = false) String workplace,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(value = "photo", required = false) MultipartFile photo) throws IOException {

        String photoPath = null;

        if (photo != null) {
            photoPath = savePhoto(photo);  // 새 사진 저장
        }

        // 수정할 정보 DTO로 변환
        SignupRequestDTO signupRequestDTO = new SignupRequestDTO(name, email, null, photoPath, interest, workplace, phoneNumber);

        // 사용자 정보 업데이트
        String result = userService.updateUserInfo(userId, signupRequestDTO);

        if (result.equals("사용자 정보 업데이트 성공!")) {
            return ResponseEntity.status(200).body(result);
        } else {
            return ResponseEntity.status(400).body(result);
        }
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            // 세션 무효화
            request.getSession().invalidate();

            return "로그아웃 성공";

        } catch (Exception e) {

            return "로그아웃 실패: " + e.getMessage();
        }
    }

}
