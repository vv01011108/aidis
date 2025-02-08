package com.example.social.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;      // 사용자 고유 ID
    private String name;
    private String interest;
    private String workplace;

    @Column(unique = true, nullable = false)
    private String email;
    private String phoneNumber;
    private String photo;  // 사진 경로

    @Getter
    @Setter
    @JsonIgnore
    private String password; // 암호화된 비밀번호

}
