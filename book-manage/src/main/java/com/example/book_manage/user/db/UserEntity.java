package com.example.book_manage.user.db;
import com.example.book_manage.book.db.BookEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    private String email;
    private String password; // 암호화된 비밀번호

    @OneToMany(mappedBy = "user") // BookEntity와의 관계 설정
    private List<BookEntity> books;
}
