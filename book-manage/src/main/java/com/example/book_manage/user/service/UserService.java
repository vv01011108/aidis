package com.example.book_manage.user.service;

import com.example.book_manage.user.db.UserEntity;
import com.example.book_manage.user.db.UserRepository;
import com.example.book_manage.user.exception.UserException;
import com.example.book_manage.user.model.UserRegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public String registerUser(UserRegisterRequest userRegisterRequest) {
        // 이메일로 이미 존재하는 사용자 확인
        System.out.println("이메일: " + userRegisterRequest.getEmail());

        if (userRepository.findByEmail(userRegisterRequest.getEmail()).isPresent()) {
            System.out.println("중복된 이메일 확인: " + userRegisterRequest.getEmail());
            throw new UserException("이미 존재하는 이메일입니다.");

        }

        String encodedPassword = passwordEncoder.encode(userRegisterRequest.getPassword());

        UserEntity userEntity = UserEntity.builder()
                .email(userRegisterRequest.getEmail())
                .password(encodedPassword)
                .name(userRegisterRequest.getName())
                .build();

        userRepository.save(userEntity);

        return "가입 완료";
    }

    public UserEntity loginUser(String email, String password) {
        UserEntity userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(password, userEntity.getPassword())) {
            throw new UserException("잘못된 비밀번호입니다.");
        }

        return userEntity; // 로그인 성공 시 UserEntity 반환
    }
}
