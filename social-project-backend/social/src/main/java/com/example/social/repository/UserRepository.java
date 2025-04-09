package com.example.social.repository;

import com.example.social.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일로 사용자 찾기
    Optional<User> findByEmail(String email);

    // 사용자 이름으로 검색 (대소문자 구분하지 않음)
    List<User> findByNameContainingIgnoreCase(String name);
}
