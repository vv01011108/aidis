package com.example.social.service;

import com.example.social.dto.SignupRequestDTO;
import com.example.social.dto.LoginRequestDTO;
import com.example.social.entity.User;
import com.example.social.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Autowired
    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    // 회원가입 처리
    public String registerUser(SignupRequestDTO signupRequestDTO) {
        // 이메일 중복 체크
        Optional<User> existingUser = userRepository.findByEmail(signupRequestDTO.getEmail());
        if (existingUser.isPresent()) {
            return "이미 존재하는 이메일입니다.";
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(signupRequestDTO.getPassword());

        // User 엔티티 저장
        User user = new User();
        user.setName(signupRequestDTO.getName());
        user.setInterest(signupRequestDTO.getInterest());
        user.setWorkplace(signupRequestDTO.getWorkplace());
        user.setEmail(signupRequestDTO.getEmail());
        user.setPhoneNumber(signupRequestDTO.getPhoneNumber());
        user.setPhoto(signupRequestDTO.getPhoto());
        user.setPassword(encodedPassword);

        userRepository.save(user);
        return "회원가입 성공!";
    }

    // 로그인 인증
    public User authenticate(String email, String password) {

        Optional<User> optionalUser = userRepository.findByEmail(email);

        // 사용자가 없으면 예외를 던짐
        if (!optionalUser.isPresent()) {
            throw new IllegalArgumentException("이메일이 존재하지 않습니다.");
        }

        User user = optionalUser.get();

        // 비밀번호가 일치하지 않으면 예외를 던짐
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }

        return user;
    }

    public User getUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId); // 데이터베이스에서 사용자 찾기
        return user.orElse(null); // 사용자가 존재하지 않으면 null 반환
    }


    // 사용자 이름으로 검색
    public List<User> searchUsersByName(String name) {
        return userRepository.findByNameContainingIgnoreCase(name);
    }


    // 사용자 정보 업데이트
    public String updateUserInfo(Long userId, SignupRequestDTO signupRequestDTO) {
        // 사용자 존재 여부 확인
        Optional<User> existingUser = userRepository.findById(userId);
        if (!existingUser.isPresent()) {
            return "사용자를 찾을 수 없습니다.";
        }

        User user = existingUser.get();

        // 수정할 정보 업데이트
        if (signupRequestDTO.getName() != null) {
            user.setName(signupRequestDTO.getName());
        }
        if (signupRequestDTO.getEmail() != null) {
            user.setEmail(signupRequestDTO.getEmail());
        }
        if (signupRequestDTO.getInterest() != null) {
            user.setInterest(signupRequestDTO.getInterest());
        }
        if (signupRequestDTO.getWorkplace() != null) {
            user.setWorkplace(signupRequestDTO.getWorkplace());
        }
        if (signupRequestDTO.getPhoneNumber() != null) {
            user.setPhoneNumber(signupRequestDTO.getPhoneNumber());
        }
        if (signupRequestDTO.getPhoto() != null) {
            user.setPhoto(signupRequestDTO.getPhoto());
        }

        // 사용자 정보 업데이트
        userRepository.save(user);
        return "사용자 정보 업데이트 성공!";
    }
}
