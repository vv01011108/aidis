package com.example.social.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class SignupRequestDTO {
    private String name;
    private String email;
    private String password;
    private String photo;  // photo는 URL 또는 경로로 저장
    private String interest;
    private String workplace;
    private String phoneNumber;

    public SignupRequestDTO(String name, String email, String password, String photo,
                            String interest, String workplace, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.photo = photo;
        this.interest = interest;
        this.workplace = workplace;
        this.phoneNumber = phoneNumber;
    }
}
