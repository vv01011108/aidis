package com.example.social.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender; // 친구 요청을 보낸 사용자

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver; // 친구 요청을 받은 사용자

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status; // 요청 상태 (PENDING, ACCEPTED, REJECTED)

    public enum Status {
        PENDING, ACCEPTED, REJECTED
    }
}
